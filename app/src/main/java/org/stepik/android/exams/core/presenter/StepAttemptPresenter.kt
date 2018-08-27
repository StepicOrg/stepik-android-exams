package org.stepik.android.exams.core.presenter

import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.db.data.StepInfo
import org.stepik.android.exams.data.model.SubmissionRequest
import org.stepik.android.exams.data.model.SubmissionResponse
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.web.AttemptRequest
import org.stepik.android.exams.web.AttemptResponse
import org.stepik.android.model.Reply
import org.stepik.android.model.Step
import org.stepik.android.model.Submission
import org.stepik.android.model.attempts.Attempt
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class StepAttemptPresenter
@Inject
constructor(
        private var stepicRestService: StepicRestService,
        @MainScheduler
        private var mainScheduler: Scheduler,
        @BackgroundScheduler
        private var backgroundScheduler: Scheduler,
        private var sharedPreferenceHelper: SharedPreferenceHelper,
        private var stepDao: StepDao
) : PresenterBase<AttemptView>() {
    private var step: Step? = null
    private var disposable = CompositeDisposable()
    private var shouldUpdate = false

    private var viewState: AttemptView.State = AttemptView.State.FirstLoading
        set(value) {
            field = value
            view?.setState(value)
        }

    private var submission: Submission? = null
    private var attempt: Attempt? = null

    override fun attachView(view: AttemptView) {
        super.attachView(view)
        view.onNeedShowAttempt(attempt)
        view.setSubmission(submission)
        view.setState(viewState)
    }

    fun checkStepExistence(reply: Reply, submissions: Submission?) {
        disposable.add(Observable
                .fromCallable {
                    stepDao.findStepById(step?.id ?: 0)
                }
                .map {
                    it.attempt != null
                }
                .onErrorReturn { false }
                .subscribeOn(backgroundScheduler)
                .subscribe { exist ->
                    submission = if (exist && !shouldUpdate) {
                        Submission(reply, attempt?.id ?: 0, null)
                    } else submissions
                    updateStepInDb(step?.id, attempt, submission)
                })
    }

    fun checkStep(step: Step) {
        Maybe.concat(checkStepIdDb(step), checkStepApi(step)).take(1)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe { pair ->
                        pair.first?.let { attempts ->
                            this.step = step
                            attemptLoaded(attempts)
                        }
                        pair.second?.let { sub ->
                            submission = sub
                            onSubmissionLoaded(submission)
                        }
                }
    }

    private fun checkStepIdDb(step: Step) =
            Maybe.fromCallable { stepDao.findStepById(step.id) }
                    .filter { (it.attempt != null || it.submission != null) }
                    .map { it.attempt to it.submission }

    private fun checkStepApi(step: Step) =
                checkAttempts(step).switchIfEmpty(createNewAttempt(step))
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .map { it.first.attempts.firstOrNull() to it.second }
                        .doOnSuccess { (attempt, submission) ->
                            updateStepAttempt(step, attempt, submission)
                        }


    private fun checkAttempts(step: Step) =
            stepicRestService
                    .getExistingAttempts(step.id, sharedPreferenceHelper.getCurrentUserId() ?: 0)
                    .filter { it.attempts.isNotEmpty() }
                    .flatMap { attemptResponse ->
                        getSubmissions(attemptResponse.attempts.first())
                                .toMaybe()
                                .map { submission ->
                                    attemptResponse to submission
                                }
                    }

    private fun getSubmissions(attempt: Attempt?) =
            stepicRestService.getSubmissions(attempt?.id ?: 0, "desc")
                    .map {
                        if (it.submissions?.isEmpty() == true) Submission()
                        else it.firstSubmission
                    }


    private fun createNewAttempt(step: Step) =
            stepicRestService.createNewAttempt(AttemptRequest(step.id)).toObservable()
                        .filter { it.attempts.isNotEmpty() }
                        .map { AttemptResponse(it.attempts) to Submission() }
                        .firstElement()

    fun createAttempt(step: Step) {
        createNewAttempt(step)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe {
                    attemptLoaded(it.first.attempts.firstOrNull())
                    updateStepAttempt(step, it.first.attempts.firstOrNull(), null)
                    shouldUpdate = false
                }
    }

    private fun attemptLoaded(it: Attempt?) {
        attempt = it
        attempt?.let {
            view?.onNeedShowAttempt(attempt)
            viewState = AttemptView.State.Success
        }
    }

    private fun updateStepAttempt(step: Step, attempt: Attempt?, submission: Submission?) =
            updateStep(step.id, attempt, submission)

    private fun updateStep(id: Long?, attempt: Attempt?, submission: Submission?) =
            disposable.add(Completable.fromCallable {
                stepDao.updateStep(StepInfo(id, attempt, submission, false))
            }
                    .subscribeOn(backgroundScheduler)
                    .subscribe())

    fun createSubmission(id: Long, reply: Reply) {
        viewState = AttemptView.State.Loading
        submission = Submission(reply, id, null)
        disposable.add(stepicRestService.createSubmission(SubmissionRequest(submission))
                .andThen(stepicRestService.getSubmissions(submission?.attempt ?: 0, "desc"))
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ onSubmissionLoaded(it) }, { onError() }))
    }

    private fun onSubmissionLoaded(submissionResponse: SubmissionResponse) {
        submission = submissionResponse.firstSubmission
        submission?.let { s ->
            if (s.status == Submission.Status.EVALUATION) {
                disposable.add(stepicRestService.getSubmissions(s.attempt, "desc")
                        .delay(1, TimeUnit.SECONDS)
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .subscribe({ onSubmissionLoaded(it) }, { onError() }))
            } else {
                updateStep(step?.id, attempt, submission)
                onSubmissionLoaded(s)
            }
        }
    }

    fun updateStepInDb(id: Long?, attempt: Attempt?, submission: Submission?) =
            updateStep(id, attempt, submission)

    private fun onSubmissionLoaded(s: Submission?) {
        viewState = AttemptView.State.Success
        view?.setSubmission(submission)
        checkSubmissionState(s)
    }

    private fun checkSubmissionState(submission: Submission?) {
        if (submission?.status == Submission.Status.CORRECT) {
            viewState = AttemptView.State.CorrectAnswerState
            shouldUpdate = true
        }
        if (submission?.status == Submission.Status.WRONG) {
            viewState = AttemptView.State.WrongAnswerState
            shouldUpdate = true
        }
    }

    private fun onError() {
        viewState = AttemptView.State.NetworkError
    }

    override fun destroy() {
        disposable.clear()
    }
}