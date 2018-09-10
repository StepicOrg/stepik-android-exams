package org.stepik.android.exams.core.presenter

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.core.interactor.GraphInteractor
import org.stepik.android.exams.core.presenter.contracts.TopicsListView
import org.stepik.android.exams.data.repository.TopicsRepository
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
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
        private val graphInteractor: GraphInteractor
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
                        .toObservable()
                        .doOnNext {data -> topicsRepository.joinCourse(data) }
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .subscribe({ data ->
                            viewState = TopicsListView.State.Success(data.topics)
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