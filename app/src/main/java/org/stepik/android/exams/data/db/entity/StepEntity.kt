package org.stepik.android.exams.data.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import org.stepik.android.model.Actions
import org.stepik.android.model.Block
import java.util.*
/*,
        foreignKeys = [ForeignKey(
                entity = ProgressEntity::class,
                parentColumns = ["stepId"],
                childColumns = ["id"])])*/
@Entity(tableName = "StepEntity")
class StepEntity(
            @PrimaryKey(autoGenerate = false)
            val id: Long,
            val lesson: Long,
            val position: Long,
            val status: org.stepik.android.model.Step.Status?,
            var block: Block?,
            val progress: String?,
            val subscriptions: List<String>?,

            val viewedBy: Long,
            val passedBy: Long,

            val createDate: Date?,
            val updateDate: Date?,

            var isCustomPassed: Boolean,
            val actions: Actions?,

            var discussionsCount: Int,
            var discussionProxy: String?,
            val hasSubmissionRestriction: Boolean,
            val maxSubmissionCount: Int
    )