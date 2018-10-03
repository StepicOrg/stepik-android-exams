package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables.zip
import io.reactivex.rxkotlin.toObservable
import io.reactivex.subjects.BehaviorSubject
import org.stepik.android.exams.core.helper.GraphHelper
import org.stepik.android.exams.core.interactor.contacts.ProgressInteractor
import org.stepik.android.exams.core.presenter.contracts.TopicsListView
import org.stepik.android.exams.data.model.TopicAdapterItem
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.data.repository.LessonsRepository
import org.stepik.android.exams.data.repository.TopicsRepository
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.LessonProgressUpdatesBus
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.model.Topic
import org.stepik.android.exams.util.addDisposable
import org.stepik.android.exams.util.then
import javax.inject.Inject
import kotlin.properties.Delegates

class TopicsListPresenter
@Inject
constructor(
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler,

        private val topicsRepository: TopicsRepository,
        private val graphHelper: GraphHelper,
        private val progressInteractor: ProgressInteractor,
        private val lessonsRepository: LessonsRepository,
        private val sharedPreferenceHelper: SharedPreferenceHelper,

        @LessonProgressUpdatesBus
        lessonProgressUpdatesBus: BehaviorSubject<Long>
) : PresenterBase<TopicsListView>() {
    private val compositeDisposable = CompositeDisposable()

    private var viewState by Delegates.observable(TopicsListView.State.Idle as TopicsListView.State) { _, _, newState ->
        view?.setState(newState)
    }

    init {
        compositeDisposable.add(lessonProgressUpdatesBus.subscribe { lessonId ->
            updateProgressFromBus(lessonId)
        })
        getGraphData()
    }

    fun getGraphData() {
        val oldState = viewState
        viewState = if (oldState is TopicsListView.State.Success) {
            TopicsListView.State.Refreshing(oldState.topics)
        } else {
            TopicsListView.State.Loading
        }
        compositeDisposable.add(
                graphHelper.getGraphData()
                        .flatMap { data -> topicsRepository.joinCourse(data).then(Single.just(data)) }
                        .flatMapObservable { it.topics.toObservable() }
                        .flatMap { topic ->
                            loadTopicsAdapterInfo(topic)
                        }
                        .toList()
                        .doOnSuccess { sharedPreferenceHelper.firstLoading = false }
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .subscribe({ data ->
                            viewState = TopicsListView.State.Success(data)
                        }, {
                            viewState = TopicsListView.State.NetworkError
                        }))
    }

    private fun loadTopicsAdapterInfo(topic: Topic): Observable<TopicAdapterItem> {
        val observableProgress: Single<Int> =
                if (sharedPreferenceHelper.firstLoading) {
                    progressInteractor.loadTopicProgressFromApi(topic.id)
                } else {
                    progressInteractor.loadTopicProgressFromDb(topic.id)
                }
        return zip(
                lessonsRepository.resolveTimeToComplete(topic.id).toObservable(),
                observableProgress.toObservable()
        ) { time: Long, progress: Int -> TopicAdapterItem(topic, time, progress) }
    }

    private fun updateProgressFromBus(lessonId: Long) {
        compositeDisposable addDisposable lessonsRepository.findTopicByLessonId(lessonId)
                .flatMap { topicId ->
                    progressInteractor.loadTopicProgressFromDb(topicId).map { topicId to it }
                }
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ (topicId, progress) ->
                    val state = viewState
                    if (state is TopicsListView.State.Success) {
                        val topics = state.topics.map { item ->
                            if (item.topic.id == topicId) {
                                item.copy(progress = progress)
                            } else {
                                item
                            }
                        }
                        viewState = TopicsListView.State.Success(topics)
                    }
                }, {})
    }

    override fun attachView(view: TopicsListView) {
        super.attachView(view)
        view.setState(viewState)
    }

    override fun destroy() {
        compositeDisposable.clear()
    }
}