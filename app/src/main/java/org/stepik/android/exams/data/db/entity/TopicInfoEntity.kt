package org.stepik.android.exams.data.db.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class TopicInfoEntity(
        @Embedded
        val topic: TopicEntity,
        @Embedded
        val graphLesson: GraphLessonEntity,
        val isJoined: Boolean
) {
    @PrimaryKey(autoGenerate = true)
     var id: Long? = null
}