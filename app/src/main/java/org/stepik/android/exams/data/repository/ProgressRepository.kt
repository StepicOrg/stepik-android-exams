package org.stepik.android.exams.data.repository

import io.reactivex.Single
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.data.db.dao.ProgressDao
import org.stepik.android.exams.data.db.entity.ProgressEntity
import org.stepik.android.exams.data.model.ProgressesResponse
import javax.inject.Inject

class ProgressRepository
@Inject
constructor(
        private val stepicRestService: StepicRestService,
        private val progressDao: ProgressDao
) {
    fun getProgressApi(progress: Array<String>): Single<ProgressesResponse> =
            stepicRestService.getProgresses(progress)

    fun insertProgresses(progressEntity: List<ProgressEntity>) =
           progressDao.insertStepProgress(progressEntity)

    fun getStepsProgressLocalByTopic(topicId: String): Single<List<Boolean>> =
            progressDao.getStepsLocalProgressByTopicId(topicId)

    fun getStepProgressLocal(stepId: Long): Single<Boolean> =
            progressDao.getStepProgress(stepId)
}