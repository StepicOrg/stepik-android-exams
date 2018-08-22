package org.stepik.android.exams.adaptive.core.presenter

import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import org.stepik.android.exams.App
import org.stepik.android.exams.adaptive.core.contracts.CardView
import org.stepik.android.exams.adaptive.listeners.AdaptiveReactionListener
import org.stepik.android.exams.adaptive.listeners.AnswerListener
import org.stepik.android.exams.adaptive.model.Card
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.core.presenter.PresenterBase
import org.stepik.android.exams.data.model.SubmissionRequest
import org.stepik.android.exams.data.model.SubmissionResponse
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.model.Submission
import org.stepik.android.model.adaptive.Reaction
import retrofit2.HttpException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CardPresenter(
        val card: Card,
        private val listener: AdaptiveReactionListener?,
        private val answerListener: AnswerListener?
) : PresenterBase<CardView>() {
    @Inject
    lateinit var stepicRestService: StepicRestService

    @Inject
    @field:MainScheduler
    lateinit var mainScheduler: Scheduler

    @Inject
    @field:BackgroundScheduler
    lateinit var backgroundScheduler: Scheduler

    private var submission: Submission? = null
    private var error: Throwable? = null

    private var disposable: Disposable? = null

    var isLoading = false
        private set

    init {
        App.componentManager().adaptiveComponent
                .inject(this)
    }

    override fun attachView(view: CardView) {
        super.attachView(view)
        view.setStep(card.step)
        view.setTitle(card.lesson?.title)
        view.setQuestion(card.step?.block?.text)
        view.getQuizViewDelegate().setAttempt(card.attempt)

        if (isLoading) view.onSubmissionLoading()
        submission?.let { view.setSubmission(it, false) }
        error?.let { onError(it) }
    }

    fun detachView() {
        view?.let {
            if (submission == null || submission?.status == Submission.Status.LOCAL) {
                submission = Submission(it.getQuizViewDelegate().createReply(), 0, Submission.Status.LOCAL) // cache current choices state
            }
            super.detachView(it)
        }
    }

    override fun destroy() {
        card.recycle()
        disposable?.dispose()
    }

    fun createReaction(reaction: Reaction) {
        val lesson = card.lessonId
        listener?.createReaction(lesson, reaction)
    }

    fun createSubmission() {
        if (disposable == null || disposable?.isDisposed != false) {
            view?.onSubmissionLoading()
            isLoading = true
            error = null

            val submission = Submission(reply = view?.getQuizViewDelegate()?.createReply(), attempt = card.attempt?.id
                    ?: 0)
            disposable = stepicRestService.createSubmission(SubmissionRequest(submission))
                    .andThen(stepicRestService.getSubmissions(submission.attempt, "desc"))
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .subscribe({ onSubmissionLoaded(it) }, { onError(it) })
        }
    }

    fun retrySubmission() {
        submission = null
    }

    private fun onSubmissionLoaded(submissionResponse: SubmissionResponse) {
        submission = submissionResponse.submissions?.firstOrNull()
        submission?.let {
            if (it.status == Submission.Status.EVALUATION) {
                disposable = stepicRestService.getSubmissions(it.attempt, "desc")
                        .delay(1, TimeUnit.SECONDS)
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .subscribe(this::onSubmissionLoaded, this::onError)
            } else {
                isLoading = false

                if (it.status == Submission.Status.CORRECT) {
                    listener?.createReaction(card.lessonId, Reaction.SOLVED)
                    answerListener?.onCorrectAnswer(it.id)
                    card.onCorrect()
                }
                if (it.status == Submission.Status.WRONG) {
                    answerListener?.onWrongAnswer(it.id)
                }

                view?.setSubmission(it, true)
            }
        }
    }

    private fun onError(error: Throwable) {
        isLoading = false
        this.error = error
        if (error is HttpException) {
            view?.onSubmissionRequestError()
        } else {
            view?.onSubmissionConnectivityError()
        }
    }
}