package org.stepik.android.exams.data.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.stepik.android.model.Reply
import org.stepik.android.model.Submission
import java.util.*

@Entity(tableName = "SubmissionEntity")
class SubmissionEntity(
        val id: Long,
        val status: Submission.Status?,
        val score: String?,
        val hint: String?,
        val time: Date?,
        val reply: Reply?,
        @PrimaryKey(autoGenerate = false)
        val attempt: Long,
        val session: String?,
        val eta: String?
)