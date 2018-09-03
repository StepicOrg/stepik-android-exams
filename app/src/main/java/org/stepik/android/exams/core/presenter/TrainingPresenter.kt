package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Observable.zip
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import org.stepik.android.exams.core.presenter.contracts.TrainingView
import org.stepik.android.exams.data.model.LessonType
import org.stepik.android.exams.data.repository.StepsRepository
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
        private val stepsRepository: StepsRepository,
        private val topicsRepository: TopicsRepository
) : PresenterBase<TrainingView>() {
    private val compositeDisposable = CompositeDisposable()
    private var theoryLessons: List<LessonType.Theory> = listOf()
    private var practiceLessons: List<LessonType.Practice> = listOf()
    private lateinit var topicsList: List<String>
    private var viewState by Delegates.observable(TrainingView.State.Idle as TrainingView.State) { _, _, newState ->
        view?.setState(newState)
    }

    init {
        loadTopics()
    }

    private fun loadTopics() {
        val oldState = viewState
        viewState = if (oldState is TrainingView.State.Success) {
            TrainingView.State.Refreshing(oldState.theory, oldState.practice)
        } else {
            TrainingView.State.Loading
        }
        topicsRepository.getGraphData()
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe { data ->
                    topicsRepository.joinCourse(data)
                            .subscribeOn(backgroundScheduler)
                            .observeOn(mainScheduler)
                            .subscribe {
                                topicsList = topicsRepository.getTopicsList()
                                loadAllLessons()
                            }
                }
    }

    private fun loadAllLessons() {
        zip<List<LessonType.Theory>, List<LessonType.Practice>, Pair<List<LessonType.Theory>, List<LessonType.Practice>>>(
                loadTheoryLessons(),
                loadPracticeLessons(),
                BiFunction { t1, t2 -> Pair(t1, t2) })
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ lessons ->
                    theoryLessons = lessons.first
                    practiceLessons = lessons.second
                    viewState = TrainingView.State.Success(theoryLessons, practiceLessons)
                }, { onError() })
    }

    private fun loadTheoryLessons() =
            Observable.merge(topicsList.map { stepsRepository.loadTheoryLesson(it) }).toList().toObservable()

    private fun loadPracticeLessons() =
            Observable.merge(topicsList.map { stepsRepository.getPracticeCoursesId(it).toObservable() }).toList().toObservable()

    override fun attachView(view: TrainingView) {
        view.setState(viewState)
        super.attachView(view)
    }

    private fun onError() {
        viewState = TrainingView.State.NetworkError
    }

    override fun destroy() {
        compositeDisposable.clear()
    }
}