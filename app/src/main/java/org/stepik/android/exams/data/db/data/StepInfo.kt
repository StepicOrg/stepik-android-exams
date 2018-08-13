package org.stepik.android.exams.data.db.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import org.stepik.android.exams.data.db.converters.GsonConverter
import org.stepik.android.exams.data.model.Submission
import org.stepik.android.exams.data.model.attempts.Attempt

@Entity(tableName = "StepInfo")
data class StepInfo(
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "id") val id: Long?,
        @TypeConverters(GsonConverter::class)
        @ColumnInfo(name = "attempt") val attempt: Attempt?,
        @TypeConverters(GsonConverter::class)
        @ColumnInfo(name = "submission") val submission: Submission?,
        @ColumnInfo(name = "isPassed") val isPassed: Boolean
)