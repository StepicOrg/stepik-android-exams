package org.stepik.android.exams.ui.steps

import android.widget.Button
import org.stepik.android.exams.data.model.Reply
import org.stepik.android.exams.data.model.Submission
import org.stepik.android.exams.data.model.attempts.Attempt

abstract class AttemptDelegate : StepDelegate() {
    protected abstract var isEnabled: Boolean
    abstract var actionButton: Button?
    abstract fun setAttempt(attempt: Attempt?)
    abstract fun setSubmission(submission: Submission?)
    abstract fun createReply(): Reply
    abstract fun blockUIBeforeSubmit(enabled : Boolean)
}