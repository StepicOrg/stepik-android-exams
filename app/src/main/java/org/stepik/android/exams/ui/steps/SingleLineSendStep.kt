package org.stepik.android.exams.ui.steps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import kotlinx.android.synthetic.main.attempt_container_layout.view.*
import kotlinx.android.synthetic.main.button_container.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.data.model.attempts.Attempt

open class SingleLineSendStep(
        step: Step?
) : StepAttemptDelegate(step) {

    protected lateinit var answerField: EditText

    override fun onCreateView(parent: ViewGroup): View {
        val parentContainer = super.onCreateView(parent) as ViewGroup
        answerField = LayoutInflater.from(parent.context).inflate(R.layout.view_single_line_attempt, parent, false) as EditText
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_single_line_attempt, parent, false)
        answerField.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                parentContainer.stepAttemptSubmitButton.performClick()
                handled = true
            }
            handled
        }
        parentContainer.attempt_container.addView(view)
        parentContainer.attempt_container.visibility = View.VISIBLE
        return parentContainer
    }

    override fun showAttempt(attempt: Attempt?) {
        answerField.text.clear()
    }

    override fun blockUIBeforeSubmit(needBlock: Boolean) {
        answerField.isEnabled = !needBlock
    }

}