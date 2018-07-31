package org.stepik.android.exams.ui.steps

import android.app.Activity
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
import org.stepik.android.exams.data.model.Reply
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.data.model.Submission
import org.stepik.android.exams.data.model.attempts.Attempt
import org.stepik.android.exams.ui.listeners.AnswerListener
import org.stepik.android.exams.util.resolvers.text.TextResolver
import javax.inject.Inject

open class StepAttemptDelegate(
        step: Step?
) : StepDelegate(step), AnswerListener{
    protected var attempt: Attempt? = null

    protected var submissions: Submission? = null

    protected open var actionButton : Button? = null

    @Inject
    lateinit var stepAttemptPresenter: StepAttemptPresenter

    @Inject
    lateinit var textResolver: TextResolver

    lateinit var context: Activity

    private lateinit var parentContainer: ViewGroup

    private lateinit var answerField : TextView

    private lateinit var attemptContainer : ViewGroup

    init {
        App.component().inject(this)
    }

    private fun setTextToActionButton(actionButton: Button?, text: String) {
        actionButton?.text = text
    }

    override fun onCreateView(parent: ViewGroup): View {
        parentContainer = super.onCreateView(parent) as ViewGroup
        answerField = parentContainer.answer_status_text
        return parentContainer
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        attemptContainer = parentContainer.attempt_container
        parentContainer.buttonsContainer.visibility = View.VISIBLE
        parentContainer.stepAttemptSubmitButton.visibility = View.VISIBLE
        actionButton = view.stepAttemptSubmitButton
        setTextToActionButton(actionButton, view.resources.getString(R.string.submit))
        actionButton?.setOnClickListener {
            if (submissions != null && submissions?.status != Submission.Status.LOCAL) {
                clearAttemptContainer()
                tryAgain()
            }
            else makeSubmission()
        }
        context = view.context as Activity
    }

    private fun tryAgain(){
        submissions = null
        stepAttemptPresenter.createNewAttempt(step)
    }

    private fun clearAttemptContainer(){
        setTextToActionButton(actionButton, context.getString(R.string.submit))
        attemptContainer.setBackgroundColor(context.resources.getColor(R.color.white))
        answerField.visibility = View.GONE
        blockUIBeforeSubmit(false)
    }

    private fun makeSubmission() {
        if (attempt == null || attempt?.id ?: 0 <= 0) return
        blockUIBeforeSubmit(true)
        val attemptId = attempt?.id ?: 0
        val reply = generateReply()
        stepAttemptPresenter.answerListener = this
        stepAttemptPresenter.createSubmission(attemptId, reply)
    }

    protected open fun startLoading(step: Step?) {
        stepAttemptPresenter.createNewAttempt(step)
    }

    protected open fun showAttempt(attempt: Attempt?) {
        this.attempt = attempt
    }

    protected open fun generateReply(): Reply {
        return Reply()
    }

    override fun onCorrectAnswer() {
        setTextToActionButton(actionButton, context.getString(R.string.next))
        attemptContainer.setBackgroundColor(context.resources.getColor(R.color.correct_answer_background))
        answerField.setText(R.string.correct)
        answerField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_correct, 0, 0, 0)
        answerField.visibility = View.VISIBLE
    }

    override fun onWrongAnswer() {
        setTextToActionButton(actionButton, context.getString(R.string.try_again))
        attemptContainer.setBackgroundColor(context.resources.getColor(R.color.wrong_answer_background))
        answerField.setText(R.string.wrong)
        answerField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0)
        answerField.visibility = View.VISIBLE
    }

    protected open fun blockUIBeforeSubmit(needBlock: Boolean) {
        answerField.isEnabled = !needBlock
    }

    protected open fun onRestoreSubmission() {}

    protected open fun onPause() {}

    protected open fun getCorrectString(): String = context.getString(R.string.correct_free_response)

}