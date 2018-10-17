package org.stepik.android.exams.data.model

import org.stepik.android.model.attempts.Attempt

class AttemptRequest(
        val attempt: Attempt
) {
    constructor(stepId: Long) : this(Attempt(step = stepId))
}
