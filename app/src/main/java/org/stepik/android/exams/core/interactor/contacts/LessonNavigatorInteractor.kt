package org.stepik.android.exams.core.interactor.contacts

interface LessonNavigatorInteractor {
    fun resolveNextLesson(lesson: Long)
    fun resolvePrevLesson(lesson: Long)
}