package org.stepik.android.exams.data.db.mapping

import org.stepik.android.exams.data.db.data.TheoryLessonWrapper
import org.stepik.android.exams.data.model.LessonTheoryWrapper

fun LessonTheoryWrapper.toEntity(): TheoryLessonWrapper =
        TheoryLessonWrapper(lesson.toEntity(), stepsList.map { it.toEntity() }, topicId, courseId)

fun TheoryLessonWrapper.toObject(): LessonTheoryWrapper =
        LessonTheoryWrapper(lessonEntity.toObject(), stepsList.map { it.toObject() }, topicId, course)