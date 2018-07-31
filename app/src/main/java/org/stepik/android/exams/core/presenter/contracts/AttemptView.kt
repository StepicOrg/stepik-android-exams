package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.data.model.Submission
import org.stepik.android.exams.data.model.attempts.Attempt

interface AttemptView {
    fun onNeedShowAttempt(attempt: Attempt?)
    fun setSubmission(submission: Submission?)
    fun onCorrectAnswer()
    fun onWrongAnswer()
}