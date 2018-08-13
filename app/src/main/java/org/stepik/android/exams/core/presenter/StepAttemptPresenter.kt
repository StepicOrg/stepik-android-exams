package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.db.data.StepInfo
import org.stepik.android.exams.data.model.*
import org.stepik.android.exams.data.model.attempts.Attempt
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.ui.listeners.AnswerListener
import org.stepik.android.exams.web.AttemptRequest
import org.stepik.android.exams.web.AttemptResponse
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

    fun checkStepExistance(): Boolean {
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

    fun checkStepInDb(step: Step?) {
        Observable.fromCallable {
            stepDao.findStepById(step?.id ?: 0)
        }
                .onErrorReturn { StepInfo(null, null, null, false) }
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe { stepInfo ->
                    if (stepInfo.attempt != null || stepInfo.submission != null) {
                        stepInfo.attempt?.let { attempts ->
                            this.step = step
                            attempt = attempts
                            view?.onNeedShowAttempt(attempts)
                        }
                        stepInfo.submission?.let { sub ->
                            submission = sub
                            onSubmissionLoaded(submission as Submission)
                        }
                    } else checkExistingAttempts(step)
                }
    }

    private fun checkExistingAttempts(step: Step?) {
        stepicRestService
                .getExistingAttempts(step?.id ?: 0, api.getCurrentUserId() ?: 0)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .onErrorReturnItem(AttemptResponse(listOf()))
                .doOnSuccess {
                    if (it.attempts.isEmpty())
                        createNewAttempt(step)
                    else attemptLoaded(it.attempts.firstOrNull())
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
                                    .subscribe { response ->
                                        submission = response.submissions?.first()
                                        onSubmissionLoaded(submission as Submission)
                                    })
                }.subscribe()

    }

    fun createNewAttempt(step: Step?) {
        this.step = step
        view?.updateSubmission(shouldUpdate = false)
        disposable.add(
                stepicRestService.createNewAttempt(AttemptRequest(step?.id ?: 0)).toObservable()
                        .filter { it.attempts.isNotEmpty() }
                        .map { it.attempts.first() }
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .subscribe { (this::attemptLoaded)(it) })
    }

    private fun attemptLoaded(it: Attempt?) {
        attempt = it
        attempt?.let {
            view?.onNeedShowAttempt(attempt)
            addStepToDb(step?.id, attempt, null, false)
            viewState = AttemptView.State.Success
        }
    }

    fun addStepToDb(id: Long?, attempt: Attempt?, submission: Submission?, needUpdate: Boolean) =
            disposable.add(Observable.fromCallable {
                if (!needUpdate)
                    stepDao.insertStep(StepInfo(id, attempt, submission, false))
                else stepDao.updateStep(StepInfo(id, attempt, submission, false))
            }
                    .subscribeOn(backgroundScheduler)
                    .subscribe())

    fun createSubmission(id: Long, reply: Reply) {
        submission = Submission(reply, id)
        disposable.add(stepicRestService.createSubmission(SubmissionRequest(submission))
                .andThen(stepicRestService.getSubmissions(submission?.attempt ?: 0, "desc"))
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe(this::onSubmissionLoaded, this::onError))
    }

    private fun onSubmissionLoaded(submissionResponse: SubmissionResponse) {
        submission = submissionResponse.firstSubmission
        submission?.let {
            if (it.status == Submission.Status.EVALUATION) {
                stepicRestService.getSubmissions(it.attempt, "desc")
                        .delay(1, TimeUnit.SECONDS)
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .subscribe(this::onSubmissionLoaded, this::onError)
            } else {
                addStepToDb(step?.id, attempt, submission, true)
                view?.updateSubmission(shouldUpdate = true)
                onSubmissionLoaded(it)
            }
        }
    }

    private fun onSubmissionLoaded(s: Submission) {
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
        } else return
    }

    private fun onError(error: Throwable) {
        viewState = AttemptView.State.NetworkError
    }

    override fun destroy() {
        disposable.clear()
    }
}