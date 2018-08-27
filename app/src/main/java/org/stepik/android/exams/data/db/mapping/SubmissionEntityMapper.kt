package org.stepik.android.exams.data.db.mapping

import org.stepik.android.exams.data.db.entity.SubmissionEntity
import org.stepik.android.model.Submission

fun Submission.toEntity(): SubmissionEntity =
        SubmissionEntity(id, status, score, hint, time, reply, attempt, session, eta)

fun SubmissionEntity.toObject(): Submission =
        Submission(id, status, score, hint, time, reply, attempt, session, eta)