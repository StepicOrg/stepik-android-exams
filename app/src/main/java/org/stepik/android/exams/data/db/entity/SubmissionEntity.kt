package org.stepik.android.exams.data.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.stepik.android.model.Reply
import org.stepik.android.model.Submission

@Entity(tableName = "SubmissionEntity")
class SubmissionEntity(
        @PrimaryKey(autoGenerate = false)
        val id: Long = 0,
        val status: Submission.Status? = null,
        val score: String? = null,
        val hint: String? = null,
        val time: String? = null,
        val reply: Reply? = null,
        val attempt: Long = 0,
        val session: String? = null,
        val eta: String? = null
)