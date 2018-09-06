package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables.zip
import org.stepik.android.exams.core.presenter.contracts.TrainingView
import org.stepik.android.exams.data.repository.StepsRepository
import org.stepik.android.exams.data.repository.TopicsRepository
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.model.Topic
import javax.inject.Inject
import kotlin.properties.Delegates

class TrainingPresenter
@Inject
constructor(
        @MainScheduler
        private var mainScheduler: Scheduler,
        @BackgroundScheduler
        private var backgroundScheduler: Scheduler,
        private val stepsRepository: StepsRepository,
        private val topicsRepository: TopicsRepository
) : PresenterBase<TrainingView>() {
    private val compositeDisposable = CompositeDisposable()
    private var viewState by Delegates.observable(TrainingView.State.Idle as TrainingView.State) { _, _, newState ->
        view?.setState(newState)
    }

    init {
        loadTopics()
    }

    fun loadTopics() {
        val oldState = viewState
        viewState = if (oldState is TrainingView.State.Success) {
            TrainingView.State.Refreshing(oldState.theory, oldState.practice)
        } else {
            TrainingView.State.Loading
        }
        compositeDisposable.add(
                topicsRepository.getGraphData()
                        .flatMap{ data ->
                            topicsRepository.joinCourse(data)
                        }
                        .flatMapObservable {
                            loadAllLessons(topicsRepository.getTopicsList())
                        }
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .subscribe({
                            (theoryLessons, practiceLessons) ->
                            viewState = TrainingView.State.Success(theoryLessons, practiceLessons)
                        }, {
                            onError()
                        }))
    }

    private fun loadAllLessons(topicsList : List<String>) =
                zip(loadTheoryLessons(topicsList), loadPracticeLessons(topicsList))

    private fun loadTheoryLessons(topicsList : List<String>) =
            Observable.merge(topicsList.map { stepsRepository.loadTheoryLesson(it) }).toList().toObservable()

    private fun loadPracticeLessons(topicsList : List<String>) =
            Observable.merge(topicsList.map { stepsRepository.getPracticeCoursesId(it).toObservable() }).toList().toObservable()

    override fun attachView(view: TrainingView) {
        super.attachView(view)
        view.setState(viewState)
    }

    private fun onError() {
        viewState = TrainingView.State.NetworkError
    }

    override fun destroy() {
        compositeDisposable.clear()
    }
}