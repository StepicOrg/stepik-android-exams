package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.api.StepikRestService
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.db.data.StepInfo
import org.stepik.android.exams.data.model.*
import org.stepik.android.exams.data.model.attempts.Attempt
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.ui.listeners.AnswerListener
import org.stepik.android.exams.web.AttemptRequest
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class StepAttemptPresenter
@Inject
constructor(
        private var stepikRestService: StepikRestService,
        @MainScheduler
        private var mainScheduler: Scheduler,
        @BackgroundScheduler
        private var backgroundScheduler: Scheduler,
        private var api: Api,
        private var stepDao: StepDao
) : PresenterBase<AttemptView>() {
    private var step: Step? = null
    private var disposable = CompositeDisposable()

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
        view.setState(viewState)
    }

    fun checkStepInDb(step: Step?) {
        Observable.fromCallable {
            stepDao.findStepById(step?.id ?: 0)
        }
                .onErrorReturn { StepInfo(null, null, null) }
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
                            view?.setSubmission(sub)
                            checkSubmissionState(sub)
                        }
                    } else {
                        createNewAttempt(step)
                    }
                }
    }

    fun createNewAttempt(step: Step?) {
        this.step = step
        disposable.add(Observable.concat(
                stepikRestService.getExistingAttempts(step?.id ?: 0, api.getCurrentUserId()
                        ?: 0).toObservable(),
                stepikRestService.createNewAttempt(AttemptRequest(step?.id ?: 0)).toObservable()
        )
                .filter { it.attempts.isNotEmpty() }
                .take(1)
                .map { it.attempts.firstOrNull() }
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe { (this::attemptLoaded)(it) })
    }

    private fun attemptLoaded(it: Attempt?) {
        viewState = AttemptView.State.Success
        attempt = it
        addStepToDb(step?.id, attempt, null, false)
        view?.onNeedShowAttempt(attempt)
    }

    private fun addStepToDb(id: Long?, attempt: Attempt?, submission: Submission?, needUpdate: Boolean) =
            Observable.fromCallable {
                if (!needUpdate)
                    stepDao.insertStep(StepInfo(id, attempt, submission))
                else stepDao.updateStep(StepInfo(id, attempt, submission))
            }
                    .subscribeOn(backgroundScheduler)
                    .subscribe()

    fun createSubmission(id: Long, reply: Reply) {
        submission = Submission(reply, id)
        disposable.add(stepikRestService.createSubmission(SubmissionRequest(submission))
                .andThen(stepikRestService.getSubmissions(submission?.attempt ?: 0, "desc"))
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe(this::onSubmissionLoaded, this::onError))
    }

    private fun onSubmissionLoaded(submissionResponse: SubmissionResponse) {
        submission = submissionResponse.firstSubmission
        submission?.let {
            if (it.status == Submission.Status.EVALUATION) {
                stepikRestService.getSubmissions(it.attempt, "desc")
                        .delay(1, TimeUnit.SECONDS)
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .subscribe(this::onSubmissionLoaded, this::onError)
            } else {
                addStepToDb(step?.id, attempt, submission, true)
                view?.setSubmission(submission)
                checkSubmissionState(it)
            }
        }
    }

    private fun checkSubmissionState(submission: Submission?) {
        viewState = if (submission?.status == Submission.Status.CORRECT) {
            AttemptView.State.CorrectAnswerState
        } else {
            AttemptView.State.WrongAnswerState
        }
    }

    private fun onError(error: Throwable) {
        viewState = AttemptView.State.NetworkError
    }

    override fun destroy() {
        disposable.clear()
    }
}