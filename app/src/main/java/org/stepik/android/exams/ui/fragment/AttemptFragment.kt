package org.stepik.android.exams.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.support.annotation.StringRes
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.answer_layout.view.*
import kotlinx.android.synthetic.main.button_container.view.*
import kotlinx.android.synthetic.main.step_delegate.*
import org.stepik.android.exams.R
import org.stepik.android.exams.api.Errors
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.ui.listeners.AnswerListener
import org.stepik.android.exams.ui.listeners.RoutingViewListener
import org.stepik.android.exams.ui.steps.AttemptDelegate
import org.stepik.android.exams.ui.steps.StepDelegate
import org.stepik.android.exams.util.changeVisibillity
import org.stepik.android.model.Step
import org.stepik.android.model.Submission
import org.stepik.android.model.attempts.Attempt


class AttemptFragment :
        StepFragment(),
        AnswerListener,
        AttemptView {

    private var attempt: Attempt? = null
    private var submissions: Submission? = null
    private var actionButton: Button? = null
    private lateinit var stepDelegate: StepDelegate
    lateinit var context: Activity
    private lateinit var answerField: TextView
    private lateinit var routingViewListener: RoutingViewListener
    private var shouldUpdate = false

    companion object {
        fun newInstance(step: Step?, id: String, lastPosition : Int): AttemptFragment {
            val args = Bundle()
            args.putString("id", id)
            args.putParcelable("step", step)
            args.putInt("last_position", lastPosition)
            val fragment = AttemptFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun resolveStep() {
        stepDelegate = stepTypeResolver.getStepDelegate(step)
        attemptContainer.addView(stepDelegate.createView(parentContainer))
        answerField = parentContainer.answer_status_text
    }

    override fun setState(state: AttemptView.State): Unit = when (state) {
        is AttemptView.State.FirstLoading -> {
            showRefreshView()
            startLoading(step)
        }

        is AttemptView.State.Idle -> {
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

    private fun setTextToActionButton(actionButton: Button?, text: String) {
        actionButton?.text = text
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resolveStep()
        loadUI(view)
        actionButton?.setOnClickListener {
            if (submissions?.status?.name == "CORRECT"
                    || submissions?.status?.name == "WRONG") {
                clearAttemptContainer()
                tryAgain()
            } else makeSubmission()
        }
        nestedScrollView.isFillViewport = true
        context = view?.context as Activity
    }

    override fun onStart() {
        super.onStart()
        presenter?.attachView(this)
    }

    override fun onStop() {
        presenter?.detachView(this)
        super.onStop()
    }

    override fun updateSubmission(shouldUpdate: Boolean) {
        this.shouldUpdate = shouldUpdate
    }

    override fun onDestroyView() {
        val reply = (stepDelegate as AttemptDelegate).createReply()
        val stepExist = presenter?.checkStepExistence() ?: false
        if (stepExist && !shouldUpdate)
            submissions = Submission(reply, attempt?.id ?: 0, null)
        presenter?.updateStepInDb(step?.id, attempt, submissions)
        super.onDestroyView()
    }

    private fun loadUI(view: View?) {
        parentContainer.buttonsContainer.visibility = View.VISIBLE
        parentContainer.stepAttemptSubmitButton.visibility = View.VISIBLE
        actionButton = view?.stepAttemptSubmitButton
        setTextToActionButton(actionButton, view?.resources?.getString(R.string.submit) ?: "")
    }

    private fun tryAgain() {
        shouldUpdate = false
        submissions = null
        presenter?.createNewAttempt(step as Step)
    }

    private fun clearAttemptContainer() {
        setTextToActionButton(actionButton, context.getString(R.string.submit))
        attemptContainer.setBackgroundColor(context.resources.getColor(R.color.white))
        answerField.visibility = View.GONE
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
        shouldUpdate = true
        if (attempt == null || attempt?.id ?: 0 <= 0 && step?.isCustomPassed != true) return
        blockUIBeforeSubmit(false)
        val attemptId = attempt?.id ?: 0
        val reply = (stepDelegate as AttemptDelegate).createReply()
        presenter?.answerListener = this
        presenter?.createSubmission(attemptId, reply)
    }


    private fun startLoading(step: Step?) {
        presenter?.checkStep(step as Step)
    }

    override fun onCorrectAnswer() {
        blockUIBeforeSubmit(false)
        setTextToActionButton(actionButton, context.getString(R.string.next))
        actionButton?.setOnClickListener {
            routingViewListener.scrollNext(step?.position?.toInt() ?: 0)
        }
        onNext()
        attemptContainer.setBackgroundColor(context.resources.getColor(R.color.correct_answer_background))
        answerField.setText(R.string.correct)
        answerField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_correct, 0, 0, 0)
        answerField.visibility = View.VISIBLE
    }

    private fun onNext() {
        routingViewListener = parentFragment as RoutingViewListener
        actionButton?.setOnClickListener {
            // if (step?.is_last == true)
            // navigatePresenter.navigateToLesson(step, id, move = true)
            /* else*/ routingViewListener.scrollNext(step?.position?.toInt() ?: 0)
        }
    }

    override fun onWrongAnswer() {
        blockUIBeforeSubmit(false)
        setTextToActionButton(actionButton, context.getString(R.string.try_again))
        attemptContainer.setBackgroundColor(context.resources.getColor(R.color.wrong_answer_background))
        answerField.setText(R.string.wrong)
        answerField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0)
        answerField.visibility = View.VISIBLE
    }

    private fun blockUIBeforeSubmit(enable: Boolean) {
        (stepDelegate as AttemptDelegate).blockUIBeforeSubmit(enable)
    }
}