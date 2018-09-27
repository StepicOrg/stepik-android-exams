package org.stepik.android.exams.data.db.pojo

import android.arch.persistence.room.Embedded
import org.stepik.android.exams.data.db.entity.VideoEntity
import org.stepik.android.model.code.CodeOptions

class BlockPojo(
        var name: String? = null,
        var text: String? = null,
        @Embedded
        var video: VideoEntity? = null,
        var options: CodeOptions? = null
)