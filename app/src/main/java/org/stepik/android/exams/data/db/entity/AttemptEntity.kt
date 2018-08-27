package org.stepik.android.exams.data.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.stepik.android.model.attempts.Dataset

@Entity(tableName = "AttemptEntity")
class AttemptEntity(
        @PrimaryKey(autoGenerate = false)
        val id: Long,
        val step: Long,
        val user: Long,

        val dataset: Dataset?,
        val datasetUrl: String?,

        val status: String?,
        val time: String?,

        val timeLeft: String?
)