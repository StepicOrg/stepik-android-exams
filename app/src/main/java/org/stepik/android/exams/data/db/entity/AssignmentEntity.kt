package org.stepik.android.exams.data.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
class AssignmentEntity(
        @PrimaryKey(autoGenerate = false)
        val id: Long,
        val step: Long,
        val unit: Long,
        val progress: String?,
        val createDate: Date?,
        val updateDate: Date?
)