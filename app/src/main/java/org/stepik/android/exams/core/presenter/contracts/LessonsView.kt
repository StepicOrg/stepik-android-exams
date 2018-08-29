package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.data.model.LessonWrapper

interface LessonsView {
    fun setState(state: LessonsView.State)

    sealed class State {
        object Idle : State()
        object Loading : State()
        class Success(val lessons: List<LessonWrapper>) : State()
        class Refreshing(val lessons: List<LessonWrapper>) : State()
        object NetworkError : State()
    }
}