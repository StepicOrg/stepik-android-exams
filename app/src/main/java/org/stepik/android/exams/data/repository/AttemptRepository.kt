package org.stepik.android.exams.data.repository

import io.reactivex.Maybe
import io.reactivex.Single
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.web.AttemptRequest
import org.stepik.android.model.attempts.Attempt
import javax.inject.Inject

class AttemptRepository
@Inject
constructor(
        private var stepicRestService: StepicRestService,
        private var sharedPreferenceHelper: SharedPreferenceHelper
) {
    fun createAttempt(stepId: Long): Single<Attempt> =
            stepicRestService
                    .createNewAttempt(AttemptRequest(stepId))
                    .map { it.attempts.first() }

    fun getAttemptByStepId(stepId: Long): Maybe<Attempt> =
            stepicRestService
                    .getExistingAttempts(stepId, sharedPreferenceHelper.getCurrentUserId() ?: -1)
                    .flatMapMaybe {
                        val attempt = it.attempts.firstOrNull()
                        if (attempt != null) {
                            Maybe.just(attempt)
                        } else {
                            Maybe.empty()
                        }
                    }

    fun resolveAttemptForStep(stepId: Long): Single<Attempt> =
            getAttemptByStepId(stepId)
                    .switchIfEmpty(createAttempt(stepId))
}