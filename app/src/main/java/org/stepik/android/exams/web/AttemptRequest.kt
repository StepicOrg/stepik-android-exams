package org.stepik.android.exams.web

import org.stepik.android.model.attempts.Attempt

class AttemptRequest(
        val attempt: Attempt
) {
    constructor(stepId: Long) : this(Attempt(step = stepId))
}
