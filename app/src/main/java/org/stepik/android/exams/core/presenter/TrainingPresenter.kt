package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import org.stepik.android.exams.core.presenter.contracts.TrainingView
import org.stepik.android.exams.data.repository.StepsRepository
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.Graph
import java.util.function.BiFunction
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
        Observable.fromIterable(
                topicsList
                        .map { stepsRepository.loadTheoryLesson(it) })
                .flatMap { it.toList().toObservable() }
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({
                    view?.showTheoryLessons(it)
                }, {})
    }

    private fun loadAllPracticeLessons(){
        Observable.fromIterable(
                topicsList
                        .map { stepsRepository.getPracticeCoursesId(it) })
                .flatMap { it.toObservable() }
                .toList()
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({
                    view?.showPracticeLessons(it)
                }, {})
    }

    override fun destroy() {
    }
}