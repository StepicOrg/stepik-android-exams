package org.stepik.android.exams.data.db.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "TopicInfo")
class TopicInfo (
        @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "topicId") val topicId: String,
    @ColumnInfo(name = "type") val lessonType: String,
    @ColumnInfo(name = "lessons") val lessons: LongArray
)