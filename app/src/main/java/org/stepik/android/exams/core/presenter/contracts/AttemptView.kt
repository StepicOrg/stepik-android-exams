package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.data.model.Submission
import org.stepik.android.exams.data.model.attempts.Attempt

interface AttemptView {
    fun onNeedShowAttempt(attempt: Attempt?)
    fun setSubmission(submission: Submission?)
    fun onCorrectAnswer()
    fun onWrongAnswer()
    fun updateSubmission(shouldUpdate: Boolean)
    fun setState(state: AttemptView.State)
    sealed class State {
        object FirstLoading : State()
        object Idle : State()
        object Loading : State()
        object Success : State()
        object NetworkError : State()
        object CorrectAnswerState : State()
        object WrongAnswerState : State()
    }
}