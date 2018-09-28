package org.stepik.android.exams.core.interactor

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.db.entity.ProgressEntity
import org.stepik.android.exams.data.db.mapping.toObject
import org.stepik.android.exams.data.repository.ProgressRepository
import org.stepik.android.exams.util.PercentUtil
import org.stepik.android.model.Progress
import org.stepik.android.model.Step
import javax.inject.Inject

class ProgressInteractor
@Inject
constructor(
        private val progressRepository: ProgressRepository,
        private val stepDao: StepDao
) {
    data class ProgressData(val ids: List<Long>, val lessonsList: List<Long>, val passedList: List<Boolean>, val progressList: List<String>)

    fun loadStepProgressFromDb(topicId: String): Single<Int> =
            stepDao.loadStepsByTopicId(topicId)
                    .flatMap { progressRepository.getStepsProgressLocalByTopic(topicId) }
                    .map { progressList -> PercentUtil.formatPercent(progressList.count { it }.toFloat(), progressList.size.toFloat()) }

    fun loadStepProgressFromApi(topicId: String) : Single<Int> =
            saveProgressToDb(topicId).andThen(countProgress(topicId))

    private fun loadStepsProgresses(topicId: String) =
            stepDao.loadStepsByTopicId(topicId)
                    .flatMap { steps -> progressRepository.getProgressApi(steps.map { it.progress!! }.toTypedArray()).zipWith(Single.just(steps)) }

    private fun saveProgressToDb(topicId: String) : Completable =
        loadStepsProgresses(topicId)
                .flatMapCompletable { (progress, steps) ->
                    saveProgressToDb(parseProgressData(progress.progresses, steps.map { it.toObject() }))
                }

    private fun countProgress(topicId: String): Single<Int> =
            loadStepsProgresses(topicId)
                    .map { (progress, _) -> progress.progresses.map { it.nStepsPassed.toFloat() / it.nSteps } }
                    .map { PercentUtil.formatPercent(it.sum(), it.size.toFloat()) }

    private fun parseProgressData(progresses: List<Progress>, steps: List<Step>): ProgressData {
        val ids = steps.map { it.id }
        val lessons = steps.map { it.lesson }
        val passed = progresses.map { it.isPassed }
        val progress = steps.map { it.progress!! }
        return ProgressData(ids, lessons, passed, progress)
    }

    private fun saveProgressToDb(progressData: ProgressData): Completable {
        val list = mutableListOf<ProgressEntity>()
        val (ids, lessons, passed, progress) = progressData
        for (m in 0 until ids.size)
            list.add(ProgressEntity(ids[m], lessons[m], passed[m], progress[m]))
        return Completable.fromCallable { progressRepository.insertProgresses(list) }
    }
}