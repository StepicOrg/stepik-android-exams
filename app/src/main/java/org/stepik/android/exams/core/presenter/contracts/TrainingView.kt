package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.data.model.LessonType

interface TrainingView {
    fun showTheoryLessons(theoryList : List<LessonType.Theory>)
    fun showPracticeLessons(practiceList : List<LessonType.Practice>)
}