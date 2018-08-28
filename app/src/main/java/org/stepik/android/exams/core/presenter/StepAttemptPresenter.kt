package org.stepik.android.exams.core.presenter

import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import org.stepik.android.exams.core.presenter.contracts.AttemptView
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
                .subscribeBy(onError = { onError() }, onSuccess = ::attemptLoaded)
    }

    private fun loadSubmission(attemptId: Long) =
            submissionRepository.getLatestSubmissionByAttemptId(attemptId)
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .subscribeBy(onError = { onError() }, onSuccess = ::onSubmissionLoaded)

    private fun attemptLoaded(attempt: Attempt) {
        this.attempt = attempt
        viewState = AttemptView.State.Success
        view?.onNeedShowAttempt(this.attempt)
    }


    fun createSubmission(attemptId: Long, reply: Reply) {
        viewState = AttemptView.State.Loading
        val submission = Submission(reply = reply, attempt = attemptId)
        disposable.add(submissionRepository.addSubmission(submission)
                .andThen(getCompletedSubmission(attemptId))
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribeBy(onError = { onError() }, onSuccess = ::onSubmissionLoaded))
    }

    private fun getCompletedSubmission(attemptId: Long): Maybe<Submission> =
            submissionRepository
                    .getLatestSubmissionByAttemptIdFromApi(attemptId)
                    .flatMap {
                        if (it.status == Submission.Status.EVALUATION) {
                            getCompletedSubmission(attemptId)
                                    .delay(1, TimeUnit.SECONDS)
                        } else {
                            Maybe.just(it)
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
                submission = Submission(
                        reply = it.getAttemptDelegate().createReply(),
                        attempt = attempt?.id ?: 0,
                        status = Submission.Status.LOCAL,
                        time = Date(Calendar.getInstance().timeInMillis))
            }
            super.detachView(it)
        }
    }

    override fun destroy() {
        submission?.let {
            submissionRepository.addSubmission(it)
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .subscribe()
        }
        disposable.clear()
    }
}