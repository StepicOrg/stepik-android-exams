package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.api.StepikRestService
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.data.model.*
import org.stepik.android.exams.data.model.attempts.Attempt
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.web.AttemptRequest
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import org.stepik.android.exams.ui.listeners.AnswerListener

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
    private var submission: Submission? = null
    private var attempt: Attempt? = null
    var answerListener : AnswerListener? = null
    override fun attachView(view: AttemptView) {
        super.attachView(view)
        if (attempt != null)
            view.onNeedShowAttempt(attempt)
    }

    fun createNewAttempt(step: Step?) {
        Observable.concat(
                stepikRestService.getExistingAttempts(step?.id ?: 0, api.getCurrentUserId()
                        ?: 0).toObservable(),
                stepikRestService.createNewAttempt(AttemptRequest(step?.id ?: 0)).toObservable()
        )
                .filter { it.attempts.isNotEmpty() }
                .take(1)
                .map { it.attempts.firstOrNull() }
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({
                    attempt = it
                    view?.onNeedShowAttempt(attempt)
                }, { /*onError(it)*/ })
    }

    fun createSubmission(id: Long, reply: Reply) {
        submission = Submission(reply, id)
        stepikRestService.createSubmission(SubmissionRequest(submission))
                .andThen(stepikRestService.getSubmissions(submission?.attempt ?: 0, "desc"))
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe(this::onSubmissionLoaded, this::onError)
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

    }

    override fun destroy() {

    }
}