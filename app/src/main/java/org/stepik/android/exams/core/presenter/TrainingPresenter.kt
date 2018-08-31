package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import org.stepik.android.exams.core.presenter.contracts.TrainingView
import org.stepik.android.exams.data.repository.StepsRepository
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.Graph
import javax.inject.Inject

class TrainingPresenter
@Inject
constructor(
        @MainScheduler
        private var mainScheduler: Scheduler,
        @BackgroundScheduler
        private var backgroundScheduler: Scheduler,
        private val graph: Graph<String>,
        private val stepsRepository: StepsRepository
) : PresenterBase<TrainingView>() {
    val topicsList = graph.getAllTopics()

    init {
        loadAllTheoryLessons()
        loadAllPracticeLessons()
    }

    private fun loadAllTheoryLessons() {
        Observable.merge(topicsList.map { stepsRepository.loadTheoryLesson(it) })
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .toList()
                .subscribe({
                    view?.showTheoryLessons(it)
                }, {})
    }

    private fun loadAllPracticeLessons(){
        Observable.merge(topicsList.map { stepsRepository.getPracticeCoursesId(it).toObservable() })
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .toList()
                .subscribe({
                    view?.showPracticeLessons(it)
                }, {})
    }

    override fun destroy() {
    }
}