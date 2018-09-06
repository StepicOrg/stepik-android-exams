package org.stepik.android.exams.data.model

import org.stepik.android.model.Lesson

data class LessonTheoryWrapper(val topicId: String = "", val lesson: LessonWrapper = LessonWrapper(Lesson(), listOf()))