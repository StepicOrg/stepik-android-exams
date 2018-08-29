package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.data.model.LessonTheoryWrapper

interface LessonsView {
    fun setState(state: LessonsView.State)
    sealed class Type {
        data class Theory(val lessonTheoryWrapper: LessonTheoryWrapper) : Type()
        data class Practice(val courseId: Long) : Type()
    }

    sealed class State {
        object Idle : State()
        object Loading : State()
        class Success(val lessons: List<Type>) : State()
        class Refreshing(val lessons: List<Type>) : State()
        object NetworkError : State()
    }
}