package org.stepik.android.exams.data.db.pojo

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import org.stepik.android.exams.data.db.entity.GraphLessonEntity
import org.stepik.android.exams.data.db.entity.LessonEntity
import org.stepik.android.exams.data.db.entity.StepEntity
import org.stepik.android.exams.data.db.entity.TopicEntity

class LessonTheoryWrapperPojo(
        @Embedded
        var lessonEntity: LessonEntity = LessonEntity(),
        @Relation(parentColumn = "lessonId", entityColumn = "lesson", entity = StepEntity::class)
        var stepsList: List<StepEntity> = listOf(),
        @Embedded
        var topic: TopicEntity = TopicEntity(),
        @Embedded
        var graphLesson: GraphLessonEntity = GraphLessonEntity()
)