package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.toObservable
import io.reactivex.subjects.BehaviorSubject
import org.stepik.android.exams.core.interactor.contacts.GraphInteractor
import org.stepik.android.exams.core.presenter.contracts.TopicsListView
import org.stepik.android.exams.data.model.TopicAdapterItem
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.data.repository.LessonsRepository
import org.stepik.android.exams.data.repository.TopicsRepository
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.model.Topic
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
        private val graphInteractor: GraphInteractor,
        private val lessonsRepository: LessonsRepository,
        private val subject: BehaviorSubject<Boolean>,
        private val sharedPreferenceHelper: SharedPreferenceHelper
) : PresenterBase<TopicsListView>() {
    private val compositeDisposable = CompositeDisposable()

    private var viewState by Delegates.observable(TopicsListView.State.Idle as TopicsListView.State) { _, _, newState ->
        view?.setState(newState)
    }

    init {
        subject.subscribe { isPassed ->
            if (isPassed) {
                getGraphData()
            }
        }
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
                graphInteractor.getGraphData()
                        .flatMap { data -> topicsRepository.joinCourse(data).then(Single.just(data)) }
                        .flatMapObservable { it.topics.toObservable() }
                        .flatMap { topic ->
                            loadTopicsAdapterInfo(topic)
                        }
                        .toList()
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .subscribe({ data ->
                            viewState = TopicsListView.State.Success(data)
                        }, {
                            viewState = TopicsListView.State.NetworkError
                        }))
    }

    private fun loadTopicsAdapterInfo(topic: Topic): Observable<TopicAdapterItem> {
        val observableProgress =
                if (sharedPreferenceHelper.firstLoading)
                    loadProgressFromApi(topic)
                else loadProgressFromDb(topic)
        return Observable.zip(
                lessonsRepository.resolveTimeToComplete(topic.id),
                observableProgress,
                BiFunction { time: Long, progress: Int -> TopicAdapterItem(topic, time, progress) })
    }

    private fun loadProgressFromApi(topic: Topic): Observable<Int> =
            lessonsRepository.loadStepProgressApi(topic.id)
                    .doOnNext { sharedPreferenceHelper.firstLoading = false }

    private fun loadProgressFromDb(topic: Topic): Observable<Int> =
            lessonsRepository.loadStepProgressFromDb(topic.id)

    override fun attachView(view: TopicsListView) {
        super.attachView(view)
        view.setState(viewState)
    }

    override fun destroy() {
        compositeDisposable.clear()
    }
}