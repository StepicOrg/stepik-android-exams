package org.stepik.android.exams.ui.steps

import android.widget.Button
import org.stepik.android.exams.data.model.Reply
import org.stepik.android.exams.data.model.Submission

interface QuizDelegate {
    var isEnabled: Boolean
    var actionButton: Button?
    fun setSubmission(submission: Submission?)
    fun createReply(): Reply
}