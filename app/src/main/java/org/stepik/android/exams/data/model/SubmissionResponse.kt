package org.stepik.android.exams.data.model

class SubmissionResponse {
    val submissions: List<Submission>? = null

    val firstSubmission: Submission?
        get() = if (submissions != null && submissions.size > 0) submissions[0] else null
}