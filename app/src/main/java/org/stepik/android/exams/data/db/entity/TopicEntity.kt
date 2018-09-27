package org.stepik.android.exams.data.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.stepik.android.exams.graph.model.GraphLesson

@Entity
class TopicEntity(
        val topicId: String,
        @ColumnInfo(name = "type") val lessonType: GraphLesson.Type,
        val lesson: Long,
        val course: Long,
        val isJoined: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}