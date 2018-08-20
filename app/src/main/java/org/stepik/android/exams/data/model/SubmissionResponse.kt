package org.stepik.android.exams.data.model

import org.stepik.android.model.Submission

class SubmissionResponse(
        val submissions: List<Submission>? = null
) {
    val firstSubmission: Submission?
        get() = submissions?.firstOrNull()
}