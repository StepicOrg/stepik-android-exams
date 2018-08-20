package org.stepik.android.exams.core.presenter

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
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

    private var viewState: AttemptView.State = AttemptView.State.Idle
        set(value) {
            field = value
            view?.setState(value)
        }

    init {
        viewState = AttemptView.State.FirstLoading
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
        Observable.fromCallable {
            stepDao.findStepById(step?.id ?: 0)
        }
                .onErrorReturn { StepInfo(null, null, null, false) }
                .map {
                    it.attempt != null
                }
                .subscribeOn(backgroundScheduler)
                .subscribe { exist ->
                    submission = if (exist && !shouldUpdate) {
                        Submission(reply, attempt?.id ?: 0, null)
                    } else submissions
                    updateStepInDb(step?.id, attempt, submission)
                }
    }

    fun checkStep(step: Step) {
        Observable.concat(checkStepIdDb(step), checkStepApi(step))
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe { stepInfo: Any ->
                    if (stepInfo is StepInfo) {
                        stepInfo.attempt?.let { attempts ->
                            this.step = step
                            attemptLoaded(attempts)
                        }
                        stepInfo.submission?.let { sub ->
                            submission = sub
                            onSubmissionLoaded(submission as Submission)
                        }
                    }
                }
    }

    private fun checkStepIdDb(step: Step) =
            Maybe.fromCallable { stepDao.findStepById(step.id) }
                    .filter { return@filter (it.attempt != null || it.submission != null) }
                    .toObservable()

    private fun checkStepApi(step: Step) =
            Observable.fromCallable {
                Observable.concat(checkAttempts(step).toObservable(), getSubmissions())
                        .map {
                            if (it is AttemptResponse && it.attempts.isEmpty())
                                createNewAttempt(step)
                        }
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .subscribe { it: Any ->
                            if (it is AttemptResponse) {
                                attemptLoaded(it.attempts.firstOrNull())
                                updateStepAttempt(step)
                            }
                            if (it is SubmissionResponse) {
                                submission = it.submissions?.first()
                                onSubmissionLoaded(submission as Submission)
                            }
                        }
            }

    private fun checkAttempts(step: Step) =
            stepicRestService
                    .getExistingAttempts(step.id, sharedPreferenceHelper.getCurrentUserId() ?: 0)
                    .onErrorReturnItem(AttemptResponse(listOf()))

    private fun getSubmissions(): Observable<SubmissionResponse> {
        if (attempt == null) return Observable.empty()
        return stepicRestService.getSubmissions(attempt?.id ?: 0, "desc")
                .filter { it.submissions?.isNotEmpty() ?: false }
    }


    fun createNewAttempt(step: Step) {
        viewState = AttemptView.State.Loading
        this.step = step
        disposable.add(
                stepicRestService.createNewAttempt(AttemptRequest(step.id)).toObservable()
                        .filter { it.attempts.isNotEmpty() }
                        .map { it.attempts.first() }
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .subscribe {
                            shouldUpdate = false
                            attemptLoaded(it)
                            updateStepAttempt(step)
                        })
    }

    private fun attemptLoaded(it: Attempt?) {
        attempt = it
        attempt?.let {
            view?.onNeedShowAttempt(attempt)
            viewState = AttemptView.State.Success
        }
    }

    private fun updateStepAttempt(step: Step) =
            updateStep(step.id, attempt, null)

    private fun updateStep(id: Long?, attempt: Attempt?, submission: Submission?) =
            disposable.add(Completable.fromCallable {
                stepDao.updateStep(StepInfo(id, attempt, submission, false))
            }
                    .subscribeOn(backgroundScheduler)
                    .subscribe({}, {}))

    fun createSubmission(id: Long, reply: Reply) {
        viewState = AttemptView.State.Loading
        submission = Submission(reply, id, null)
        shouldUpdate = true
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
                stepicRestService.getSubmissions(s.attempt, "desc")
                        .delay(1, TimeUnit.SECONDS)
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .subscribe({ onSubmissionLoaded(it) }, { onError() })
            } else {
                updateStep(step?.id, attempt, submission)
                shouldUpdate = true
                onSubmissionLoaded(s)
                viewState = AttemptView.State.Success
            }
        }
    }

    fun updateStepInDb(id: Long?, attempt: Attempt?, submission: Submission?) =
            updateStep(id, attempt, submission)

    private fun onSubmissionLoaded(s: Submission) {
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