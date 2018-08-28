package org.stepik.android.exams.data.db.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.stepik.android.exams.graph.model.GraphLesson

@Entity(tableName = "TopicInfo")
class TopicInfo(
        @ColumnInfo(name = "topicId") val topicId: String,
        @ColumnInfo(name = "type") val lessonType: GraphLesson.Type,
        @ColumnInfo(name = "lesson") val lesson: Long,
        @ColumnInfo(name = "course") val course: Long,
        @ColumnInfo(name = "isJoined") val isJoined: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}