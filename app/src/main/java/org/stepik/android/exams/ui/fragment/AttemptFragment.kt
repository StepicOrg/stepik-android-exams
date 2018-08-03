package org.stepik.android.exams.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.answer_layout.view.*
import kotlinx.android.synthetic.main.attempt_container_layout.view.*
import kotlinx.android.synthetic.main.button_container.view.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.StepAttemptPresenter
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.data.model.Reply
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.data.model.Submission
import org.stepik.android.exams.data.model.attempts.Attempt
import org.stepik.android.exams.ui.listeners.AnswerListener
import org.stepik.android.exams.ui.listeners.RoutingViewListener
import org.stepik.android.exams.ui.steps.AttemptDelegate
import org.stepik.android.exams.ui.steps.StepDelegate
import org.stepik.android.exams.util.resolvers.text.TextResolver
import javax.inject.Inject
import android.widget.LinearLayout



open class AttemptFragment : StepFragment(), AnswerListener, AttemptView {
    protected var attempt: Attempt? = null

    private var submissions: Submission? = null

    protected open var actionButton: Button? = null

    lateinit var stepDelegate : StepDelegate
    @Inject
    lateinit var textResolver: TextResolver

    lateinit var context: Activity

    private lateinit var answerField: TextView

    lateinit var routingViewListener: RoutingViewListener

    companion object {
        fun newInstance(step: Step?): StepFragment {
            val args = Bundle()
            args.putParcelable("step", step)
            val fragment = AttemptFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun resolveStep(){
        stepDelegate = stepTypeResolver.getStepDelegate(step)
        attemptContainer.addView(stepDelegate.createView(parentContainer))
        answerField = parentContainer.answer_status_text
        // stepDelegate.routingViewListener = routingViewListener
    }

    override fun setState(state: AttemptView.State): Unit = when (state) {
        is AttemptView.State.FirstLoading -> {
            startLoading(step)
        }

        is AttemptView.State.Idle -> {
        }

        is AttemptView.State.Loading -> {
        }

        is AttemptView.State.NetworkError -> {
        }

        is AttemptView.State.Success -> {
        }
        is AttemptView.State.CorrectAnswerState -> {
            onCorrectAnswer()
        }
        is AttemptView.State.WrongAnswerState -> {
            onWrongAnswer()
        }
    }

    private fun setTextToActionButton(actionButton: Button?, text: String) {
        actionButton?.text = text
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resolveStep()
        loadUI(view)
        actionButton?.setOnClickListener {
            if (submissions != null && submissions?.status != Submission.Status.LOCAL) {
                clearAttemptContainer()
                tryAgain()
            } else makeSubmission()
        }
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

    private fun loadUI(view: View?) {
        parentContainer.buttonsContainer.visibility = View.VISIBLE
        parentContainer.stepAttemptSubmitButton.visibility = View.VISIBLE
        actionButton = view?.stepAttemptSubmitButton
        setTextToActionButton(actionButton, view?.resources?.getString(R.string.submit) ?: "")
    }

    private fun tryAgain() {
        submissions = null
        presenter?.createNewAttempt(step)
    }

    private fun clearAttemptContainer() {
        setTextToActionButton(actionButton, context.getString(R.string.submit))
        attemptContainer.setBackgroundColor(context.resources.getColor(R.color.white))
        answerField.visibility = View.GONE
        blockUIBeforeSubmit(false)
    }

    override fun onNeedShowAttempt(attempt: Attempt?) {
        this.attempt = attempt
        (stepDelegate as AttemptDelegate).setAttempt(attempt)
    }

    override fun setSubmission(submission: Submission?) {
        submissions = submission
        (stepDelegate as AttemptDelegate).setSubmission(submission)
    }

    private fun makeSubmission() {
        if (attempt == null || attempt?.id ?: 0 <= 0) return
        blockUIBeforeSubmit(true)
        val attemptId = attempt?.id ?: 0
        val reply = (stepDelegate as AttemptDelegate).createReply()
        presenter?.answerListener = this
        presenter?.createSubmission(attemptId, reply)
    }

    protected open fun startLoading(step: Step?) {
        presenter?.createNewAttempt(step)
    }

    override fun onCorrectAnswer() {
        setTextToActionButton(actionButton, context.getString(R.string.next))
        onNext()
        attemptContainer.setBackgroundColor(context.resources.getColor(R.color.correct_answer_background))
        answerField.setText(R.string.correct)
        answerField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_correct, 0, 0, 0)
        answerField.visibility = View.VISIBLE
    }

    private fun onNext() {
        actionButton?.setOnClickListener {
            routingViewListener.scrollNext(step.position.toInt())
        }
    }

    override fun onWrongAnswer() {
        setTextToActionButton(actionButton, context.getString(R.string.try_again))
        attemptContainer.setBackgroundColor(context.resources.getColor(R.color.wrong_answer_background))
        answerField.setText(R.string.wrong)
        answerField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0)
        answerField.visibility = View.VISIBLE
    }

    private fun blockUIBeforeSubmit(block: Boolean) {
        for (i in 0 until attemptContainer.childCount) {
            val child = attemptContainer.getChildAt(i)
            child.isEnabled = !block
        }
    }
}