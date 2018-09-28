package org.stepik.android.exams.data.repository

import io.reactivex.Completable
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
    fun getProgress(progress : Array<String>) : Single<ProgressesResponse> =
            stepicRestService.getProgresses(progress)

    fun insertProgresses(progressEntity : List<ProgressEntity>)  =
           progressDao.insertStepProgress(progressEntity)

    fun getStepProgressData(topicId : String) : Single<List<String>> =
            progressDao.getAllStepsProgressDataByTopicId(topicId)

    fun getStepsProgressByTopic(topicId : String) : Single<List<Boolean>> =
            progressDao.getStepsLocalProgressByTopicId(topicId)

    fun getStepProgress(stepId : Long) =
            progressDao.getStepProgress(stepId)
}