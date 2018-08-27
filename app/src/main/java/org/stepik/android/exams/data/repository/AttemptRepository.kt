package org.stepik.android.exams.data.repository

import io.reactivex.Completable
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.data.db.dao.AttemptEntitiyDao
import org.stepik.android.exams.data.db.mapping.toEntity
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.web.AttemptRequest
import org.stepik.android.model.Step
import org.stepik.android.model.attempts.Attempt
import javax.inject.Inject

class AttemptRepository
@Inject
constructor(
        private val attemptEntitiyDao: AttemptEntitiyDao,
        private var stepicRestService: StepicRestService,
        private var sharedPreferenceHelper: SharedPreferenceHelper
) {

    fun createAttempt(attempt: Attempt) =
            Completable.fromCallable { attemptEntitiyDao.insertAttempt(attempt.toEntity()) }

    fun updateAttempt(attempt: Attempt) =
            Completable.fromCallable { attemptEntitiyDao.updateAttempt(attempt.toEntity()) }

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