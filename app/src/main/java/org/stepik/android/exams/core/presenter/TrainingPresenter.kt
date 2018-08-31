package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.core.presenter.contracts.TrainingView
import org.stepik.android.exams.data.model.LessonType
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
    private val compositeDisposable = CompositeDisposable()
    private var theoryLessons : List<LessonType.Theory> = listOf()
    private var practiceLessons : List<LessonType.Practice> = listOf()
    val topicsList = graph.getAllTopics()

    init {
        loadAllTheoryLessons()
        loadAllPracticeLessons()
    }

    private fun loadAllTheoryLessons() {
        compositeDisposable.add(Observable.merge(topicsList.map { stepsRepository.loadTheoryLesson(it) })
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .toList()
                .subscribe({ lessons ->
                    theoryLessons = lessons
                    view?.showTheoryLessons(lessons)
                }, {}))
    }

    private fun loadAllPracticeLessons(){
        compositeDisposable.add(Observable.merge(topicsList.map { stepsRepository.getPracticeCoursesId(it).toObservable() })
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .toList()
                .subscribe({lessons ->
                    practiceLessons = lessons
                    view?.showPracticeLessons(lessons)
                }, {}))
    }

    override fun attachView(view: TrainingView) {
        super.attachView(view)
        if (theoryLessons.isNotEmpty()){
            view.showTheoryLessons(theoryLessons)
        }
        if (practiceLessons.isNotEmpty()){
            view.showPracticeLessons(practiceLessons)
        }
    }

    override fun destroy() {
        compositeDisposable.clear()
    }
}