package org.stepik.android.exams.data.db.mapping

import org.stepik.android.exams.data.db.entity.StepEntity
import org.stepik.android.model.Step

fun Step.toEntity(): StepEntity =
        StepEntity(id, lesson, position, status, block?.toPojo(), progress, subscriptions, viewedBy, passedBy, createDate, updateDate, isCustomPassed, actions!!.toPojo(), discussionsCount, discussionProxy, hasSubmissionRestriction, maxSubmissionCount)

fun StepEntity.toObject(): Step =
        Step(id, lesson, position, status, block?.toObject(), progress, subscriptions, viewedBy, passedBy, createDate, updateDate, isCustomPassed, actions.toObject(), discussionsCount, discussionProxy, hasSubmissionRestriction, maxSubmissionCount)