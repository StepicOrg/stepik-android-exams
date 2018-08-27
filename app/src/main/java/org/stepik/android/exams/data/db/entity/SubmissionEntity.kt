package org.stepik.android.exams.data.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.stepik.android.model.Reply
import org.stepik.android.model.Submission

@Entity(tableName = "SubmissionEntity")
class SubmissionEntity(
        @PrimaryKey(autoGenerate = false)
        val id: Long,
        val status: Submission.Status?,
        val score: String?,
        val hint: String?,
        val time: String?,
        val reply: Reply?,
        val attempt: Long,
        val session: String?,
        val eta: String?
)