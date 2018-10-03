package org.stepik.android.exams.data.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.stepik.android.model.VideoUrl

@Entity
data class VideoEntity(
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "videoId")var id: Long = 0,
        var thumbnail: String? = null,
        var urls: List<VideoUrl>? = null,
        var duration: Long = 0
)