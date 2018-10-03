package org.stepik.android.exams.data.db.mapping

import org.stepik.android.exams.data.db.pojo.BlockPojo
import org.stepik.android.model.Block

fun Block.toPojo(): BlockPojo =
        BlockPojo(name, text, video?.toEntity(), options)

fun BlockPojo.toObject(): Block =
        Block(name, text, video?.toObject(), options)