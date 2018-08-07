package org.stepik.android.exams.ui.steps

import android.text.InputType
import android.view.View
import org.stepik.android.exams.data.model.Reply
import org.stepik.android.exams.data.model.Submission

class NumberDelegate : StringDelegate() {
    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        answerField.setRawInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
    }

    override fun setSubmission(submission: Submission?) {
        submission?.reply?.number?.let { answerField.setText(it) }
    }

    override fun createReply() = Reply(number = answerField.text.toString())

    override fun blockUIBeforeSubmit(enabled: Boolean) {
        answerField.isEnabled = enabled
    }
}