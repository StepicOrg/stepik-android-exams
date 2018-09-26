package org.stepik.android.exams.data.db.data

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import org.stepik.android.exams.data.db.entity.LessonEntity
import org.stepik.android.exams.data.db.entity.StepEntity

class TheoryLessonWrapper(
        @Embedded
        var lessonEntity: LessonEntity = LessonEntity(),
        @Relation(parentColumn = "lessonId", entityColumn = "lesson", entity = StepEntity::class)
        var stepsList: List<StepEntity> = listOf(),
        var topicId: String = "",
        var course: Long = 0L
)