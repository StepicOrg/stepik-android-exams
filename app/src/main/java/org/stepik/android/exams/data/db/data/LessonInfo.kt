package org.stepik.android.exams.data.db.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import org.stepik.android.exams.data.db.converters.GsonConverter
import org.stepik.android.exams.data.model.LessonTheoryWrapper

@Entity(tableName = "LessonInfo")
class LessonInfo(
        @ColumnInfo(name = "topicId") val topicId: String,
        @ColumnInfo(name = "lessonId") val lessonId: Long?,
        @TypeConverters(GsonConverter::class)
        @ColumnInfo(name = "lesson") val lesson: LessonTheoryWrapper?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}