package org.stepik.android.exams.data.repository

import io.reactivex.Completable
import io.reactivex.Maybe
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.data.db.dao.SubmissionEntityDao
import org.stepik.android.exams.data.db.entity.SubmissionEntity
import org.stepik.android.exams.data.db.mapping.toEntity
import org.stepik.android.exams.data.db.mapping.toObject
import org.stepik.android.exams.data.model.SubmissionRequest
import org.stepik.android.model.Submission
import javax.inject.Inject

class SubmissionRepository
@Inject
constructor(
    private val submissionEntityDao: SubmissionEntityDao,
    private val stepicRestService: StepicRestService
) {
    fun getSubmissionByAttemptId(attemptId: Long): Maybe<Submission> =
            submissionEntityDao
                    .findSubmissionByAttemptId(attemptId)
                    .map(SubmissionEntity::toObject)
                    .switchIfEmpty(getLatestSubmission(attemptId))

    fun getLatestSubmission(attemptId: Long): Maybe<Submission> =
            stepicRestService
                    .getSubmissions(attemptId, "desc")
                    .flatMapMaybe { response ->
                        val submission = response.submissions?.firstOrNull()
                        if (submission != null) {
                            Maybe.just(submission)
                        } else {
                            Maybe.empty()
                        }
                    }
                    .doOnSuccess {
                        submissionEntityDao.insertSubmission(it.toEntity())
                    }

    fun addSubmission(submission: Submission): Completable =
            if (submission.status == Submission.Status.LOCAL) {
                Completable.fromCallable { submissionEntityDao.insertSubmission(submission.toEntity()) }
            } else {
                stepicRestService.createSubmission(SubmissionRequest(submission))
            }
}