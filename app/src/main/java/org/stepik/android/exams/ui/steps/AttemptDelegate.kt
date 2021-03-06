package org.stepik.android.exams.ui.steps

import android.widget.Button
import org.stepik.android.model.Reply
import org.stepik.android.model.Submission
import org.stepik.android.model.attempts.Attempt

abstract class AttemptDelegate : StepDelegate() {
    abstract var isEnabled: Boolean
    abstract var actionButton: Button?
    abstract fun setAttempt(attempt: Attempt?)
    abstract fun setSubmission(submission: Submission?)
    abstract fun createReply(): Reply
    abstract fun blockUIBeforeSubmit(enabled: Boolean)
}