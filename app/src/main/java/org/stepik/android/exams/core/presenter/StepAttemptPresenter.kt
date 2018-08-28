package org.stepik.android.exams.core.presenter

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.data.model.SubmissionRequest
import org.stepik.android.exams.data.model.SubmissionResponse
import org.stepik.android.exams.data.repository.AttemptRepository
import org.stepik.android.exams.data.repository.SubmissionRepository
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.model.Reply
import org.stepik.android.model.Step
import org.stepik.android.model.Submission
import org.stepik.android.model.attempts.Attempt
import java.util.*
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
        private val attemptRepository: AttemptRepository,
        private val submissionRepository: SubmissionRepository
) : PresenterBase<AttemptView>() {
    private var disposable = CompositeDisposable()

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

    fun loadAttemptWithSubmission(step: Step) {
        attemptRepository.resolveAttemptForStep(step.id)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({
                    attemptLoaded(it)
                    loadSubmission(it.id)
                }, { onError() })

    }

    fun createAttempt(step: Step) {
        submission = null
        attemptRepository.createAttempt(step.id)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({
                    attemptLoaded(it)
                }, { onError() })
    }

    private fun loadSubmission(attemptId: Long) =
            submissionRepository.getLatestSubmissionByAttemptId(attemptId)
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .subscribe({
                        onSubmissionLoaded(it)
                    }, {})

    private fun attemptLoaded(it: Attempt) {
        attempt = it
        view?.onNeedShowAttempt(attempt)
        viewState = AttemptView.State.Success
    }


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
                onSubmissionLoaded(s)
            }
        }
    }

    private fun onSubmissionLoaded(submission: Submission?) {
        this.submission = submission
        viewState = AttemptView.State.Success
        view?.setSubmission(submission)
        checkSubmissionState(submission)
    }

    private fun checkSubmissionState(submission: Submission?) {
        if (submission?.status == Submission.Status.CORRECT) {
            viewState = AttemptView.State.CorrectAnswerState
        }
        if (submission?.status == Submission.Status.WRONG) {
            viewState = AttemptView.State.WrongAnswerState
        }
    }

    private fun onError() {
        viewState = AttemptView.State.NetworkError
    }


    override fun detachView(view: AttemptView) {
        view.let {
            if (submission == null || submission?.status == Submission.Status.LOCAL) {
                submission = Submission(reply = it.getAttemptDelegate().createReply(), id = attempt?.id
                        ?: 0, status = Submission.Status.LOCAL,
                        time = Date(Calendar.getInstance().timeInMillis))
                submissionRepository.addSubmission(submission!!)
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .subscribe()
            }
            super.detachView(it)
        }
    }

    override fun destroy() {
        disposable.clear()
    }
}