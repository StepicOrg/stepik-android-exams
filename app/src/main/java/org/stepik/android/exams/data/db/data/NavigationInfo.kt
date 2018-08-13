package org.stepik.android.exams.data.db.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import org.stepik.android.exams.data.db.converters.GsonConverter
import org.stepik.android.exams.data.model.Lesson

@Entity(tableName = "NavigationInfo")
class NavigationInfo(
        @ColumnInfo(name = "topicId") val topicId: String,
        @ColumnInfo(name = "lessonId") val lessonId: Long?,
        @TypeConverters(GsonConverter::class)
        @ColumnInfo(name = "lesson") val lesson: Lesson?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}