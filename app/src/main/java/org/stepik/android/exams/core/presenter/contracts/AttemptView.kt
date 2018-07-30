package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.data.model.attempts.Attempt

interface AttemptView {
    fun trySetAttempt(attempt: Attempt?)
}