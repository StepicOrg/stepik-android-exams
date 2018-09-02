package org.stepik.android.exams.core.presenter

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.api.graph.GraphService
import org.stepik.android.exams.core.presenter.contracts.TopicsListView
import org.stepik.android.exams.data.db.dao.TopicDao
import org.stepik.android.exams.data.db.data.TopicInfo
import org.stepik.android.exams.data.repository.TopicsRepository
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.Graph
import org.stepik.android.exams.graph.model.GraphData
import org.stepik.android.exams.graph.model.GraphLesson
import javax.inject.Inject
import kotlin.properties.Delegates

class TopicsListPresenter
@Inject
constructor(
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler,
        private val topicsRepository: TopicsRepository
) : PresenterBase<TopicsListView>() {
    private val compositeDisposable = CompositeDisposable()

    private var graphData: GraphData

    private var viewState by Delegates.observable(TopicsListView.State.Idle as TopicsListView.State) { _, _, newState ->
        view?.setState(newState)
    }

    init {
        graphData = GraphData()
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
                topicsRepository.getGraphData()
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ data ->
                    graphData = data
                    topicsRepository.joinCourse(graphData)
                            .subscribeOn(backgroundScheduler)
                            .observeOn(mainScheduler)
                            .subscribe()
                    viewState = TopicsListView.State.Success(graphData.topics)
                }, {
                    onError()
                }))
    }

    override fun attachView(view: TopicsListView) {
        super.attachView(view)
        view.setState(viewState)
        if (graphData.topics.isNotEmpty()) {
            view.showGraphData(graphData)
        }
    }

    private fun onError() {
        viewState = TopicsListView.State.NetworkError
    }

    override fun destroy() {
        compositeDisposable.clear()
    }
}