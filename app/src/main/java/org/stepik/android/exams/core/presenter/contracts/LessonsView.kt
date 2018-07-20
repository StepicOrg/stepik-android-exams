package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.data.model.Lesson

interface LessonsView {
    fun showLessons(lesson: List<Lesson>?)
    fun setState(state: LessonsView.State)
    sealed class State {
        object FirstLoading : State()
        object Idle : State()
        object Loading : State()
        object Success : State()
        object NetworkError : State()
    }
}