package org.stepik.android.exams.data.model

sealed class LessonType {
    data class Theory(val lessonTheoryWrapper: LessonTheoryWrapper) : LessonType()
    data class Practice(val courseId: Long) : LessonType()
}