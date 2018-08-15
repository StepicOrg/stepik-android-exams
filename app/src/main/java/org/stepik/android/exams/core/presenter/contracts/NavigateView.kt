package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.data.model.Lesson
import org.stepik.android.exams.data.model.Step

interface NavigateView {
    fun moveToLesson(id: String, lesson: Lesson?)
    fun showNavigation()
    fun hideNavigation()
    fun showTabs(steps: List<Step>)
}