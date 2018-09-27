package org.stepik.android.exams.data.db.mapping

import org.stepik.android.exams.data.db.entity.AssignmentEntity
import org.stepik.android.model.Assignment

fun Assignment.toEntity(): AssignmentEntity =
        AssignmentEntity(id, step, unit, progress, createDate, updateDate)

fun AssignmentEntity.toObject(): Assignment =
        Assignment(id, step, unit, progress, createDate, updateDate)