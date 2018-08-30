package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.data.model.LessonType

interface LessonsView {
    fun setState(state: LessonsView.State)
    sealed class State {
        object Idle : State()
        object Loading : State()
        class Success(val lessons: List<LessonType>) : State()
        class Refreshing(val lessons: List<LessonType>) : State()
        object NetworkError : State()
    }
}