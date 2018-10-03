package org.stepik.android.exams.core.interactor

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import org.stepik.android.exams.core.interactor.contacts.ProgressInteractor
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.db.entity.ProgressEntity
import org.stepik.android.exams.data.db.entity.StepEntity
import org.stepik.android.exams.data.repository.ProgressRepository
import org.stepik.android.model.Progress
import javax.inject.Inject

class ProgressInteractorImpl
@Inject
constructor(
        private val progressRepository: ProgressRepository,
        private val stepDao: StepDao
) : ProgressInteractor{
    override fun loadTopicProgressFromDb(topicId: String): Single<Int> =
            progressRepository.getStepsProgressLocalByTopic(topicId)
                    .map { progressList -> formatPercent(progressList.count { it }.toFloat(), progressList.size.toFloat()) }

    override fun loadTopicProgressFromApi(topicId: String) : Single<Int> =
            saveProgressToDb(topicId).andThen(countProgress(topicId))

    private fun loadStepsProgresses(topicId: String) =
            stepDao.loadStepsByTopicId(topicId)
                    .flatMap { steps -> progressRepository.getProgressApi(steps.map { it.progress!! }.toTypedArray()).zipWith(Single.just(steps)) }

    private fun saveProgressToDb(topicId: String) : Completable =
        loadStepsProgresses(topicId)
                .flatMapCompletable { (progress, steps) ->
                    saveProgressToDb(progress.progresses, steps)
                }

    private fun countProgress(topicId: String): Single<Int> =
            loadStepsProgresses(topicId)
                    .map { (progress, _) -> progress.progresses.map { it.nStepsPassed.toFloat() / it.nSteps } }
                    .map { formatPercent(it.sum(), it.size.toFloat()) }

    private fun saveProgressToDb(progress : List<Progress>, steps : List<StepEntity>): Completable {
        val list = mutableListOf<ProgressEntity>()
        for (m in 0 until steps.size) {
            list.add(ProgressEntity(steps[m].id, steps[m].lesson, progress[m].isPassed, steps[m].progress!!))
        }
        return Completable.fromCallable { progressRepository.insertProgresses(list) }
    }

    private fun formatPercent(first : Float, second : Float) =
            ((first / second) * 100).toInt()
}