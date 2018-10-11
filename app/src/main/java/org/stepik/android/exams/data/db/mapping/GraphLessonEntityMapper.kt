package org.stepik.android.exams.data.db.mapping

import org.stepik.android.exams.data.db.entity.GraphLessonEntity
import org.stepik.android.exams.graph.model.GraphLesson

fun GraphLesson.toEntity(): GraphLessonEntity =
        GraphLessonEntity(id, type, description, course)

fun GraphLessonEntity.toObject(): GraphLesson =
        GraphLesson(lesson, type!!, lessonDescription, course)