package org.stepik.android.exams.data.db.mapping

import org.stepik.android.exams.data.db.pojo.ActionPojo
import org.stepik.android.model.Actions

fun Actions.toPojo(): ActionPojo =
        ActionPojo(vote, delete, testSection, doReview, editInstructions)

fun ActionPojo.toObject(): Actions =
        Actions(vote, delete, testSection, doReview, editInstructions)