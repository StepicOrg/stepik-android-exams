package org.stepik.android.exams.data.repository

import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.data.db.dao.AttemptEntitiyDao
import org.stepik.android.exams.data.db.entity.AttemptEntitiy
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.web.AttemptRequest
import org.stepik.android.model.Step
import javax.inject.Inject

class AttemptRepository
@Inject
constructor(
        private val attemptEntitiyDao: AttemptEntitiyDao,
        private var stepicRestService: StepicRestService,
        private var sharedPreferenceHelper: SharedPreferenceHelper
) {

    fun createAttempt(attempt: AttemptEntitiy) =
            attemptEntitiyDao.insertAttempt(attempt)

    fun updateAttempt(attempt: AttemptEntitiy) =
            attemptEntitiyDao.updateAttempt(attempt)

    fun findAttemptInDb(attemptId: Long) =
            attemptEntitiyDao.findAttemptById(attemptId)

    fun loadExistingAttempts(step: Step) =
            stepicRestService
                    .getExistingAttempts(step.id, sharedPreferenceHelper.getCurrentUserId() ?: 0)
                    .filter { it.attempts.isNotEmpty() }
                    .map { it.attempts.first() }

    fun createNewAttempt(step: Step) =
            stepicRestService
                    .createNewAttempt(AttemptRequest(step.id))
                    .filter { it.attempts.isNotEmpty() }
                    .map { it.attempts.first() }
}