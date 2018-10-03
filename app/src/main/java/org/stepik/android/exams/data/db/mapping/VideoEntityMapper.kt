package org.stepik.android.exams.data.db.mapping

import org.stepik.android.exams.data.db.entity.VideoEntity
import org.stepik.android.model.Video

fun Video.toEntity(): VideoEntity =
        VideoEntity(id, thumbnail, urls, duration)

fun VideoEntity.toObject(): Video =
        Video(id, thumbnail, urls, duration)