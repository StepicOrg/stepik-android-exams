package org.stepik.android.exams.adaptive.core.contracts

import org.stepik.android.exams.ui.steps.AttemptDelegate
import org.stepik.android.model.Step
import org.stepik.android.model.Submission

interface CardView {
    fun setSubmission(submission: Submission, animate: Boolean)
    fun onSubmissionConnectivityError()
    fun onSubmissionRequestError()
    fun onSubmissionLoading()

    fun setTitle(title: String?)
    fun setQuestion(html: String?)
    fun setStep(step: Step?)

    fun getQuizViewDelegate(): AttemptDelegate
}