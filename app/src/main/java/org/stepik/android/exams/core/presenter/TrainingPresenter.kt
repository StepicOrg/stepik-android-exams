package org.stepik.android.exams.core.presenter

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.core.interactor.GraphInteractor
import org.stepik.android.exams.core.presenter.contracts.TrainingView
import org.stepik.android.exams.data.repository.LessonsRepository
import org.stepik.android.exams.data.repository.TopicsRepository
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import javax.inject.Inject
import kotlin.properties.Delegates

class TrainingPresenter
@Inject
constructor(
        @MainScheduler
        private var mainScheduler: Scheduler,
        @BackgroundScheduler
        private var backgroundScheduler: Scheduler,
        private val lessonsRepository: LessonsRepository,
        private val topicsRepository: TopicsRepository,
        private val graphInteractor: GraphInteractor
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
                graphInteractor.getGraphData()
                        .flatMapMaybe { data ->
                            topicsRepository.joinCourse(data)
                        }
                        .flatMapObservable {
                            lessonsRepository.loadAllLessons()
                        }
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .subscribe({ (theoryLessons, practiceLessons) ->
                            viewState = TrainingView.State.Success(theoryLessons, practiceLessons)
                        }, {
                            onError()
                        }))
    }

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