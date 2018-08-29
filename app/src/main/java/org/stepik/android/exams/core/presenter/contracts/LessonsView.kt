package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.model.Lesson
import org.stepik.android.model.Step

interface LessonsView {
    fun setState(state: LessonsView.State)
    sealed class Type{
        data class Theory(val lesson : Lesson, val stepsList : List<Step>)
        data class Practice(val courseId : Long)
    }

    sealed class State {
        object FirstLoading : State()
        object Idle : State()
        object Loading : State()
        class Success(val lessons: List<LessonWrapper>) : State()
        class Refreshing(val lessons: List<LessonWrapper>) : State()
        object NetworkError : State()
    }
}