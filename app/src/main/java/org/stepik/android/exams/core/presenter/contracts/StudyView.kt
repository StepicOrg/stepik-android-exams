package org.stepik.android.exams.core.presenter.contracts
import org.stepik.android.exams.data.model.Lesson

interface StudyView {
    fun showLessons(lesson: List<Lesson>?)
}