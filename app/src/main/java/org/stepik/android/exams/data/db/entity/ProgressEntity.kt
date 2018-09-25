package org.stepik.android.exams.data.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "ProgressEntity")
class ProgressEntity(
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "stepId") val stepId: Long,
        @ColumnInfo(name = "isPassed") val isPassed: Boolean,
        @ColumnInfo(name = "progress") val progress: String
)