package org.stepik.android.exams.data.db.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity

@Entity(tableName = "TopicInfo")
class CourseInfo (
    @ColumnInfo(name = "topicId") val courseId: String,
    @ColumnInfo(name = "type") val lessonType: String,
    @ColumnInfo(name = "lessons") val lessons: LongArray
)