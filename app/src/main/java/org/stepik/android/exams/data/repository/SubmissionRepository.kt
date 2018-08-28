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
    fun getLatestSubmissionByAttemptId(attemptId: Long): Maybe<Submission> = Maybe
            .concat(
                    submissionEntityDao
                            .findSubmissionByAttemptId(attemptId)
                            .map(SubmissionEntity::toObject),
                    getLatestSubmissionByAttemptIdFromApi(attemptId)
            )
            .toList()
            .flatMapMaybe { submissions ->
                val submission = submissions.maxBy { it.time?.time ?: 0 }
                if (submission != null) {
                    Maybe.just(submission)
                } else {
                    Maybe.empty()
                }
            }

    fun getLatestSubmissionByAttemptIdFromApi(attemptId: Long): Maybe<Submission> =
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

    fun addSubmission(submission: Submission): Completable =
            if (submission.status == Submission.Status.LOCAL) {
                Completable.fromCallable { submissionEntityDao.insertSubmission(submission.toEntity()) }
            } else {
                stepicRestService.createSubmission(SubmissionRequest(submission))
            }
}