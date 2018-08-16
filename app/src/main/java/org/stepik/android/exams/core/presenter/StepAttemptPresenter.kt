package org.stepik.android.exams.core.presenter

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.db.data.StepInfo
import org.stepik.android.exams.data.model.SubmissionRequest
import org.stepik.android.exams.data.model.SubmissionResponse
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.ui.listeners.AnswerListener
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
        private var api: Api,
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
    var answerListener: AnswerListener? = null

    override fun attachView(view: AttemptView) {
        super.attachView(view)
        view.onNeedShowAttempt(attempt)
        view.setSubmission(submission)
        view.updateSubmission(shouldUpdate)
        view.setState(viewState)
    }

    fun checkStepExistence(): Boolean {
        var needUpdate = false
        Observable.fromCallable {
            stepDao.findStepById(step?.id ?: 0)
        }
                .onErrorReturn { StepInfo(null, null, null, false) }
                .subscribeOn(backgroundScheduler)
                .blockingSubscribe {
                    needUpdate = it.attempt != null
                }
        return needUpdate
    }

    fun checkStep(step: Step) {
        Maybe.concat(checkStepIdDb(step), checkStepApi(step)).take(1).subscribe({}, { onError() })
    }

    private fun checkStepIdDb(step: Step) =
            Maybe.fromCallable {
                stepDao.findStepById(step.id)
            }
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .filter { return@filter (it.attempt != null || it.submission != null) }
                    .doOnSuccess { stepInfo ->
                        stepInfo.attempt?.let { attempts ->
                            this.step = step
                            attemptLoaded(attempts)
                        }
                        stepInfo.submission?.let { sub ->
                            submission = sub
                            onSubmissionLoaded(submission as Submission)
                        }
                    }

    private fun checkStepApi(step: Step) =
            stepicRestService
                    .getExistingAttempts(step.id, api.getCurrentUserId() ?: 0)
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .onErrorReturnItem(AttemptResponse(listOf()))
                    .doOnSuccess { it ->
                        if (it.attempts.isEmpty())
                            createNewAttempt(step)
                        else {
                            attemptLoaded(it.attempts.firstOrNull())
                            updateStepAttempt(step)
                        }
                    }
                    .toCompletable()
                    .andThen { sub ->
                        if (attempt == null) return@andThen
                        sub.onComplete()
                        sub.onSubscribe(
                                stepicRestService.getSubmissions(attempt?.id ?: 0, "desc")
                                        .filter { it.submissions?.isNotEmpty() ?: false }
                                        .subscribeOn(backgroundScheduler)
                                        .observeOn(mainScheduler)
                                        .subscribe({ response ->
                                            submission = response.submissions?.first()
                                            onSubmissionLoaded(submission as Submission)
                                        }, { onError() }))
                    }.toMaybe<Unit>()

    fun createNewAttempt(step: Step) {
        viewState = AttemptView.State.Loading
        this.step = step
        view?.updateSubmission(shouldUpdate = false)
        disposable.add(
                stepicRestService.createNewAttempt(AttemptRequest(step.id)).toObservable()
                        .filter { it.attempts.isNotEmpty() }
                        .map { it.attempts.first() }
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .subscribe {
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
            disposable.add(Observable.fromCallable {
                stepDao.updateStep(StepInfo(id, attempt, submission, false))
            }
                    .subscribeOn(backgroundScheduler)
                    .subscribe({}, {}))

    fun createSubmission(id: Long, reply: Reply) {
        viewState = AttemptView.State.Loading
        submission = Submission(reply, id, null)
        disposable.add(stepicRestService.createSubmission(SubmissionRequest(submission))
                .andThen(stepicRestService.getSubmissions(submission?.attempt ?: 0, "desc"))
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe  ({onSubmissionLoaded(it)},{onError()}))
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
                view?.updateSubmission(shouldUpdate = true)
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
            view?.updateSubmission(shouldUpdate = true)
        }
        if (submission?.status == Submission.Status.WRONG) {
            viewState = AttemptView.State.WrongAnswerState
            view?.updateSubmission(shouldUpdate = true)
        }
    }

    private fun onError() {
        viewState = AttemptView.State.NetworkError
    }

    override fun destroy() {
        disposable.clear()
    }
}