package org.stepik.android.exams.core.presenter.contracts
import org.stepik.android.exams.data.model.Lesson

interface LessonsView {
    fun showLessons(lesson: List<Lesson>?)
}