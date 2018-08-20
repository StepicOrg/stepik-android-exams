package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.data.model.LessonWrapper

interface NavigateView {
    fun moveToLesson(id: String, lesson: LessonWrapper?)
    fun showNextButton()
    fun hideNextButton()
    fun showPrevButton()
    fun hidePrevButton()
}