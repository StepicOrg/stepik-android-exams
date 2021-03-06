package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.ui.steps.AttemptDelegate
import org.stepik.android.model.Submission
import org.stepik.android.model.attempts.Attempt

interface AttemptView {
    fun onNeedShowAttempt(attempt: Attempt?)
    fun setSubmission(submission: Submission?)
    fun onCorrectAnswer()
    fun onWrongAnswer()
    fun setState(state: AttemptView.State)
    fun getAttemptDelegate(): AttemptDelegate
    sealed class State {
        object FirstLoading : State()
        object Loading : State()
        object Success : State()
        object NetworkError : State()
        object CorrectAnswerState : State()
        object WrongAnswerState : State()
    }
}