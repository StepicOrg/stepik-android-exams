package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.answer_layout.*
import kotlinx.android.synthetic.main.button_container.*
import kotlinx.android.synthetic.main.step_delegate.*
import org.stepik.android.exams.R
import org.stepik.android.exams.api.Errors
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.ui.listeners.RoutingViewListener
import org.stepik.android.exams.ui.steps.AttemptDelegate
import org.stepik.android.exams.ui.steps.StepDelegate
import org.stepik.android.exams.util.argument
import org.stepik.android.exams.util.changeVisibillity
import org.stepik.android.model.Step
import org.stepik.android.model.Submission
import org.stepik.android.model.attempts.Attempt


class AttemptFragment : StepFragment(), AttemptView {
    companion object {
        fun newInstance(step: Step, topicId: String, lastPosition: Long): AttemptFragment =
                AttemptFragment().apply {
                    this.step = step
                    this.topicId = topicId
                    this.lastPosition = lastPosition
                }
    }

    private var attempt: Attempt? = null
    private var submissions: Submission? = null

    private lateinit var stepDelegate: StepDelegate
    private lateinit var routingViewListener: RoutingViewListener

    private var step: Step by argument()
    private var topicId: String by argument()
    private var lastPosition: Long by argument()

    private fun resolveStep() {
        stepDelegate = stepTypeResolver.getStepDelegate(step)
        attemptContainer.addView(stepDelegate.createView(parentContainer))
    }

    override fun setState(state: AttemptView.State): Unit = when (state) {
        is AttemptView.State.FirstLoading -> {
            showRefreshView()
            startLoading(step)
        }

        is AttemptView.State.Loading -> {
            showRefreshView()
        }

        is AttemptView.State.NetworkError -> {
            hideRefreshView()
            onError(Errors.ConnectionProblem)
        }

        is AttemptView.State.Success -> {
            hideRefreshView()
            hideErrorMessage()
        }

        is AttemptView.State.CorrectAnswerState -> {
            onCorrectAnswer()
        }

        is AttemptView.State.WrongAnswerState -> {
            onWrongAnswer()
        }
    }

    private fun onError(error: Errors) {
        @StringRes val messageResId = when (error) {
            Errors.ConnectionProblem -> R.string.auth_error_connectivity
        }
        showErrorMessage(messageResId)
    }

    private fun showRefreshView() {
        swipeRefreshAttempt.isRefreshing = true
    }

    private fun hideRefreshView() {
        swipeRefreshAttempt.isRefreshing = false
    }

    private fun showErrorMessage(messageResId: Int) {
        errorText.setText(messageResId)
        errorText.changeVisibillity(true)
    }

    private fun hideErrorMessage() {
        errorText.changeVisibillity(false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resolveStep()

        buttonsContainer.visibility = View.VISIBLE
        stepAttemptSubmitButton.visibility = View.VISIBLE
        stepAttemptSubmitButton.setText(R.string.submit)

        stepAttemptSubmitButton.setOnClickListener {
            if (submissions?.status?.name == "CORRECT" ||
                    submissions?.status?.name == "WRONG") {
                clearAttemptContainer()
                tryAgain()
            } else {
                makeSubmission()
            }
        }
        nestedScrollView.isFillViewport = true
    }

    override fun onStart() {
        super.onStart()
        presenter?.attachView(this)
    }

    override fun onStop() {
        presenter?.detachView(this)
        super.onStop()
    }

    override fun onDestroyView() {
        val reply = (stepDelegate as AttemptDelegate).createReply()
        presenter?.checkStepExistence(reply, submissions)
        super.onDestroyView()
    }

    private fun tryAgain() {
        submissions = null
        presenter?.createAttempt(step)
    }

    private fun clearAttemptContainer() {
        stepAttemptSubmitButton.setText(R.string.submit)
        attemptContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        answerStatusText.visibility = View.GONE
        blockUIBeforeSubmit(true)
    }

    override fun onNeedShowAttempt(attempt: Attempt?) {
        this.attempt = attempt
        (stepDelegate as AttemptDelegate).setAttempt(attempt)
    }

    override fun setSubmission(submission: Submission?) {
        submissions = submission
        (stepDelegate as AttemptDelegate).setSubmission(submission)
        submission?.let {
            progressPresenter.isStepPassed(step)
        }
    }

    private fun makeSubmission() {
        if (attempt == null || attempt?.id ?: 0 <= 0 && !step.isCustomPassed) return
        blockUIBeforeSubmit(false)
        val attemptId = attempt?.id ?: 0
        val reply = (stepDelegate as AttemptDelegate).createReply()
        presenter?.createSubmission(attemptId, reply)
    }


    private fun startLoading(step: Step?) {
        presenter?.checkStep(step as Step)
    }

    override fun onCorrectAnswer() {
        blockUIBeforeSubmit(false)
        stepAttemptSubmitButton.setText(R.string.next)
        stepAttemptSubmitButton?.setOnClickListener {
            routingViewListener.scrollNext(step.position.toInt())
        }

        onNext()
        attemptContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.correct_answer_background))
        answerStatusText.setText(R.string.correct)
        answerStatusText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_correct, 0, 0, 0)
        answerStatusText.visibility = View.VISIBLE
    }

    private fun onNext() {
        routingViewListener = activity as RoutingViewListener
        stepAttemptSubmitButton?.setOnClickListener {
            if (step.position == lastPosition) {
                navigationPresenter.navigateToLesson(step, topicId, lastPosition, move = true)
            } else {
                routingViewListener.scrollNext(step.position.toInt())
            }
        }
    }

    override fun onWrongAnswer() {
        blockUIBeforeSubmit(false)
        stepAttemptSubmitButton.setText(R.string.try_again)
        attemptContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.wrong_answer_background))
        answerStatusText.setText(R.string.wrong)
        answerStatusText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0)
        answerStatusText.visibility = View.VISIBLE
    }

    private fun blockUIBeforeSubmit(enable: Boolean) {
        (stepDelegate as AttemptDelegate).blockUIBeforeSubmit(enable)
    }
}