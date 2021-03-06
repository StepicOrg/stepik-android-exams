package org.stepik.android.exams.ui.steps

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import org.stepik.android.exams.R
import org.stepik.android.model.attempts.Attempt

class FreeAnswerDelegate : StringDelegate() {

    override fun onCreateView(parent: ViewGroup) =
            LayoutInflater.from(parent.context).inflate(R.layout.view_free_answer_attempt, parent, false) as EditText

    override fun setAttempt(attempt: Attempt?) =
            answerField.text.clear()

    override fun blockUIBeforeSubmit(enabled: Boolean) {
        answerField.isEnabled = enabled
    }

}