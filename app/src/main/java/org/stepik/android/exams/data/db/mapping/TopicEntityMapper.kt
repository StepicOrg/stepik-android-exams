package org.stepik.android.exams.data.db.mapping

import org.stepik.android.exams.data.db.entity.TopicEntity
import org.stepik.android.exams.graph.model.Topic

fun Topic.toEntity(): TopicEntity =
        TopicEntity(id, title, requiredFor, description)

fun TopicEntity.toObject(): Topic =
        Topic(topicId, topicTitle, requiredFor, topicDescription)