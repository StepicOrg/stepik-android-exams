package org.stepik.android.exams.data.repository

import io.reactivex.Completable
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.data.db.dao.AttemptEntityDao
import org.stepik.android.exams.data.db.mapping.toEntity
import org.stepik.android.exams.data.db.mapping.toObject
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.web.AttemptRequest
import org.stepik.android.model.Step
import org.stepik.android.model.attempts.Attempt
import javax.inject.Inject

class AttemptRepository
@Inject
constructor(
        private val attemptEntityDao: AttemptEntityDao,
        private var stepicRestService: StepicRestService,
        private var sharedPreferenceHelper: SharedPreferenceHelper
) {

    fun createAttempt(attempt: Attempt) =
            Completable.fromCallable { attemptEntityDao.insertAttempt(attempt.toEntity()) }

    fun updateAttempt(attempt: Attempt) =
            Completable.fromCallable { attemptEntityDao.updateAttempt(attempt.toEntity()) }

    fun getAttempt(attemptId: Long, step: Step) =
            attemptEntitiyDao.findAttemptById(attemptId)
                    .map(AttemptEntity::toObject)
                    .switchIfEmpty(loadLastAttempt(step))
    fun findAttemptInDb(attemptId: Long) =
            attemptEntityDao.findAttemptById(attemptId)

    private fun loadLastAttempt(step: Step) =
            stepicRestService
                    .getExistingAttempts(step.id, sharedPreferenceHelper.getCurrentUserId() ?: 0)
                    .filter { it.attempts.isNotEmpty() }
                    .map { it.attempts.first() }
                    .switchIfEmpty(createNewAttempt(step))

    fun createNewAttempt(step: Step) =
            stepicRestService
                    .createNewAttempt(AttemptRequest(step.id))
                    .map { it.attempts.first() }
}