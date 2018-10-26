package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.graph.model.Topic

interface CourseView {
    fun initContinueEducation(topic : Topic)
}