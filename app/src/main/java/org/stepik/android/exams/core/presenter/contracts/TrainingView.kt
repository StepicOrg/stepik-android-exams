package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.data.model.LessonType

interface TrainingView {
    fun setState(state: State)
    sealed class State {
        object Idle : State()
        object Loading : State()
        class Success(val theory: List<LessonType.Theory>, val practice: List<LessonType.Practice>) : State()
        class Refreshing(val theory: List<LessonType.Theory>, val practice: List<LessonType.Practice>) : State()
        object NetworkError : State()
    }
}