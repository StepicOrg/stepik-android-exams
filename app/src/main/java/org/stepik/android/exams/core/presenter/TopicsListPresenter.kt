package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.toObservable
import org.stepik.android.exams.core.interactor.contacts.GraphInteractor
import org.stepik.android.exams.core.presenter.contracts.TopicsListView
import org.stepik.android.exams.data.model.TopicAdapterItem
import org.stepik.android.exams.data.repository.LessonsRepository
import org.stepik.android.exams.data.repository.TopicsRepository
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
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
        private val lessonsRepository: LessonsRepository
) : PresenterBase<TopicsListView>() {
    private val compositeDisposable = CompositeDisposable()

    private var viewState by Delegates.observable(TopicsListView.State.Idle as TopicsListView.State) { _, _, newState ->
        view?.setState(newState)
    }

    init {
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
                            Observable.zip(
                                    lessonsRepository.resolveTimeToComplete(topic.id),
                                    lessonsRepository.loadStepProgressByTopicId(topic.id),
                                    BiFunction { time: Long, progress: Int -> TopicAdapterItem(topic, time, progress) })
                        }
                        .toList()
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .subscribe({ data ->
                            viewState = TopicsListView.State.Success(data)
                        }, {
                            onError()
                        }))
    }

    override fun attachView(view: TopicsListView) {
        super.attachView(view)
        view.setState(viewState)
    }

    private fun onError() {
        viewState = TopicsListView.State.NetworkError
    }

    override fun destroy() {
        compositeDisposable.clear()
    }
}