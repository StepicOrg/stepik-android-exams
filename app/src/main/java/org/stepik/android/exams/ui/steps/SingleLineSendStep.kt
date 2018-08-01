package org.stepik.android.exams.ui.steps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import org.stepik.android.exams.R
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.data.model.attempts.Attempt

open class SingleLineSendStep(
        step: Step?
) : StepAttemptDelegate(step) {

    protected lateinit var answerField: EditText

    override fun onCreateView(parent: ViewGroup): View {
        super.onCreateView(parent)
        answerField = LayoutInflater.from(parent.context).inflate(R.layout.view_single_line_attempt, parent, false) as EditText
        answerField.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                actionButton?.performClick()
                handled = true
            }
            handled
        }
        attemptContainer.addView(answerField)
        attemptContainer.visibility = View.VISIBLE
        return parentContainer
    }

    override fun showAttempt(attempt: Attempt?) {
        answerField.text.clear()
    }

    override fun blockUIBeforeSubmit(needBlock: Boolean) {
        answerField.isEnabled = !needBlock
    }

}