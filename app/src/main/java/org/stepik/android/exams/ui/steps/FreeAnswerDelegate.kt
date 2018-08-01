package org.stepik.android.exams.ui.steps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.data.model.Reply
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.data.model.attempts.Attempt

class FreeAnswerDelegate(
        step: Step?
) : StepAttemptDelegate(step), AttemptView {

    lateinit var answerField: EditText

    override fun onCreateView(parent: ViewGroup): View {
        val parentContainer = super.onCreateView(parent) as ViewGroup
        answerField = LayoutInflater.from(parent.context).inflate(R.layout.view_free_answer_attempt, parent, false) as EditText
        attemptContainer.addView(answerField)
        return parentContainer
    }

    override fun showAttempt(attempt: Attempt?) {
        answerField.text.clear()
    }

    override fun generateReply(): Reply {
        var answer = answerField.text.toString()
        if (attempt?.dataset?.isHtmlEnabled == true) {
            answer = textResolver.replaceWhitespaceToBr(answer)
        }
        return Reply(text = answer, attachments = emptyList())
    }

    override fun blockUIBeforeSubmit(needBlock: Boolean) {
        answerField.isEnabled = !needBlock
    }

    override fun onRestoreSubmission() {
        val reply = submissions?.reply ?: return

        val text = reply.text
        if (attempt?.dataset?.isHtmlEnabled == true) {
            answerField.setText(textResolver.fromHtml(text))
        } else {
            answerField.setText(text)
        }
    }

    override fun onPause() {
        super.onPause()
        answerField.clearFocus()
    }
}