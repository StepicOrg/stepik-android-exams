package org.stepik.android.exams.data.db.mapping

import org.stepik.android.exams.data.db.pojo.LessonTheoryWrapperPojo
import org.stepik.android.exams.data.model.LessonTheoryWrapper

fun LessonTheoryWrapper.toPojo(): LessonTheoryWrapperPojo =
        LessonTheoryWrapperPojo(lesson.toEntity(), stepsList.map { it.toEntity() }, topic.toEntity(), graphLesson.toEntity())

fun LessonTheoryWrapperPojo.toObject(): LessonTheoryWrapper =
        LessonTheoryWrapper(lessonEntity.toObject(), stepsList.map { it.toObject() }, topic.toObject(), graphLesson.toObject())