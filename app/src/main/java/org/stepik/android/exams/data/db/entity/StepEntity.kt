package org.stepik.android.exams.data.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.stepik.android.exams.data.db.pojo.ActionPojo
import org.stepik.android.exams.data.db.pojo.BlockPojo
import java.util.*

@Entity
class StepEntity(
        @ColumnInfo(name = "step")
        val id: Long,
        val lesson: Long,
        val position: Long,
        val status: org.stepik.android.model.Step.Status?,
        @Embedded
        val block: BlockPojo?,
        val progress: String?,
        val subscriptions: List<String>?,

        val viewedBy: Long,
        val passedBy: Long,

        val createDate: Date?,
        val updateDate: Date?,

        var isCustomPassed: Boolean,
        @Embedded
        val actions: ActionPojo,

        val discussionsCount: Int,
        val discussionProxy: String?,
        val hasSubmissionRestriction: Boolean,
        val maxSubmissionCount: Int
) {
    @PrimaryKey(autoGenerate = true)
    var stepId: Long? = null
}