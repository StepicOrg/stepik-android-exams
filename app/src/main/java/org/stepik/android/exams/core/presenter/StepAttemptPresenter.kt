package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.api.StepikRestService
import org.stepik.android.exams.core.presenter.contracts.AttemptView
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
        private var api: Api
) : PresenterBase<AttemptView>() {

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
        view.setState(viewState)
    }

    fun createNewAttempt(step: Step?) {
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
        view?.onNeedShowAttempt(attempt)
    }

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
                if (it.status == Submission.Status.CORRECT) {
                    answerListener?.onCorrectAnswer()
                } else {
                    answerListener?.onWrongAnswer()
                }
                view?.setSubmission(submission)
            }
        }
    }

    private fun onError(error: Throwable) {
        viewState = AttemptView.State.NetworkError
    }

    override fun destroy() {
        disposable.clear()
    }
}