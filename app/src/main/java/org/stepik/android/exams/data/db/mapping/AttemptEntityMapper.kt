package org.stepik.android.exams.data.db.mapping

import org.stepik.android.exams.data.db.entity.AttemptEntity
import org.stepik.android.model.attempts.Attempt
import org.stepik.android.model.attempts.DatasetWrapper

fun Attempt.toEntity(): AttemptEntity =
        AttemptEntity(id, step, user, dataset, datasetUrl, status, time, timeLeft)

fun AttemptEntity.toObject(): Attempt =
        Attempt(id, step, user, DatasetWrapper(dataset), datasetUrl, status, time, timeLeft)