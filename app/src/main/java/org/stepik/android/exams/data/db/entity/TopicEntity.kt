package org.stepik.android.exams.data.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class TopicEntity(
        @PrimaryKey(autoGenerate = false)
        var topicId: String = "",
        var topicTitle: String = "",
        var requiredFor: List<String>? = null,
        var topicDescription: String? = null
)