package org.stepik.android.exams.data.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
class LessonEntity(
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "lessonId")
        var id: Long = 0,
        var steps: LongArray = longArrayOf(),
        var tags: IntArray? = null,
        var playlists: Array<String>? = null,

        var isFeatured: Boolean = false,
        var isPrime: Boolean = false,
        var progress: String? = null,
        var owner: Int = 0,
        var subscriptions: Array<String>? = null,

        var viewedBy: Int = 0,
        var passedBy: Int = 0,

        var dependencies: Array<String>? = null,
        var followers: Array<String>? = null,
        var language: String? = null,
        var isPublic: Boolean = false,
        var title: String? = null,
        var slug: String? = null,

        var createDate: Date? = null,
        var updateDate: Date? = null,

        var learnersGroup: String? = null,
        var teacherGroup: String? = null,
        var coverUrl: String? = null,

        var timeToComplete: Long = 0

)