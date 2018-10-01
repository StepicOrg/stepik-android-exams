package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.data.model.LessonTheoryWrapper

interface NavigateView {
    fun moveToLesson(id: String, lessonTheory: LessonTheoryWrapper)
    fun showNextButton()
    fun hideNextButton()
    fun showPrevButton()
    fun hidePrevButton()
}