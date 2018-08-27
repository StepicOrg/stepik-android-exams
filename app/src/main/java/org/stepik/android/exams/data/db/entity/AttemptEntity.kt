package org.stepik.android.exams.data.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.stepik.android.model.attempts.Dataset

@Entity(tableName = "AttemptEntity")
class AttemptEntity(
        @PrimaryKey(autoGenerate = false)
        val id: Long = 0,
        val step: Long = 0,
        val user: Long = 0,

        val dataset: Dataset? = null,
        val datasetUrl: String? = null,

        val status: String? = null,
        val time: String? = null,

        val timeLeft: String? = null
)