package org.stepik.android.exams.ui.steps

import android.text.InputType
import android.view.View
import org.stepik.android.exams.data.model.Reply
import org.stepik.android.exams.data.model.Step

class NumberDelegate(
        step: Step?
) : SingleLineSendStep(step) {
    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        answerField.setRawInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL)
    }

    override fun generateReply(): Reply =
            Reply(number = answerField.text.toString())
}