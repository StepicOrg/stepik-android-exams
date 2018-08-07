package org.stepik.android.exams.data.db.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import org.stepik.android.exams.data.db.converters.GsonConverter
import org.stepik.android.exams.data.model.Lesson

@Entity(tableName = "NavigationInfo")
class NavigationInfo(
        @ColumnInfo(name = "lessonId") val lessonId: Long?,
        @TypeConverters(GsonConverter::class)
        @ColumnInfo(name = "lesson") val lesson: Lesson?,
        @ColumnInfo(name = "next_lesson") val nextLesson: Long?,
        @ColumnInfo(name = "prev_lesson") val prevLesson: Long?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}