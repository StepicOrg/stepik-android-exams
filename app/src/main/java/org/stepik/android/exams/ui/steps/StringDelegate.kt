package org.stepik.android.exams.ui.steps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.data.model.Reply
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.data.model.Submission
import org.stepik.android.exams.data.model.attempts.Attempt

class StringDelegate(
        step: Step
) : StepAttemptDelegate(step), AttemptView {

    override fun onNeedShowAttempt(attempt: Attempt?) = showAttempt(attempt)

    override fun setSubmission(submission: Submission?) {
        super.submissions = submission
    }

    private lateinit var answerField: EditText

    override fun onCreateView(parent: ViewGroup): View {
        val parentContainer = super.onCreateView(parent) as ViewGroup
        answerField = LayoutInflater.from(parent.context).inflate(R.layout.view_free_answer_attempt, parent, false) as EditText
        attemptContainer.addView(answerField)
        return parentContainer
    }

    override fun showAttempt(attempt: Attempt?) {
        super.attempt = attempt
        answerField.text.clear()
    }

    override fun blockUIBeforeSubmit(needBlock: Boolean) {
        answerField.isEnabled = !needBlock
    }

    override fun generateReply(): Reply =
            Reply(text = answerField.text.toString())

    override fun onRestoreSubmission() {
        val text = submissions?.reply?.text ?: return
        answerField.setText(text)
    }

    override fun onPause() {
        super.onPause()
        answerField.clearFocus()
    }
}