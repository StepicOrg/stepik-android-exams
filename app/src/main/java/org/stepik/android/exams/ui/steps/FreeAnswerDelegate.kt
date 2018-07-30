package org.stepik.android.exams.ui.steps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import kotlinx.android.synthetic.main.attempt_container_layout.view.*
import kotlinx.android.synthetic.main.recycler_item.view.*
import kotlinx.android.synthetic.main.step_text_header.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.data.model.Reply
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.data.model.attempts.Attempt

class FreeAnswerDelegate(
         step: Step?
)  : StepAttemptDelegate(step) {

    lateinit var answerField: EditText

    override fun onCreateView(parent: ViewGroup): View {
        val parentContainer = super.onCreateView(parent) as ViewGroup
        answerField = LayoutInflater.from(parent.context).inflate(R.layout.view_free_answer_attempt, parent, false) as EditText
        parentContainer.attempt_container.addView(answerField)
        return parentContainer
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
    }

    override fun showAttempt(attempt: Attempt) {
        answerField.text.clear()
    }

    override fun generateReply(): Reply {
        var answer = answerField.text.toString()
        if (attempt._dataset?.isHtmlEnabled == true) {
            answer = textResolver.replaceWhitespaceToBr(answer)
        }

        return Reply(text = answer, attachments = emptyList())
    }

    override fun blockUIBeforeSubmit(needBlock: Boolean) {
        answerField.isEnabled = !needBlock
    }

    override fun onRestoreSubmission() {
    }

    //override fun getCorrectString(): String = getString(R.string.correct_free_response)

    override fun onPause() {
        super.onPause()
        answerField.clearFocus()
    }
}