package org.stepik.android.exams.ui.steps

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import org.stepik.android.exams.R
import org.stepik.android.exams.data.model.Reply
import org.stepik.android.exams.data.model.Submission
import org.stepik.android.exams.data.model.attempts.Attempt

class NotSupportedDelegate : AttemptDelegate() {
    override var isEnabled: Boolean = false
    override var actionButton: Button? = null
        set(value) {
            value?.setText(R.string.open_web_to_solve)
            value?.setOnClickListener { }
        }

    override fun onCreateView(parent: ViewGroup): View = View(parent.context)

    override fun setAttempt(attempt: Attempt?) {}

    override fun setSubmission(submission: Submission?) {}

    override fun createReply() = Reply()

    override fun blockUIBeforeSubmit(enabled: Boolean) {}
}