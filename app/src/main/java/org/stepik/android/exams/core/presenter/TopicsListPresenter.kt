package org.stepik.android.exams.core.presenter

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.api.graph.GraphService
import org.stepik.android.exams.core.presenter.contracts.TopicsListView
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.Graph
import org.stepik.android.exams.graph.model.GraphData
import javax.inject.Inject

class TopicsListPresenter
@Inject
constructor(
        private val graph: Graph<String>,
        private val graphService: GraphService,
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler
) : PresenterBase<TopicsListView>() {
    private val compositeDisposable = CompositeDisposable()

    private var graphData: GraphData

    private var viewState: TopicsListView.State = TopicsListView.State.Idle
        set(value) {
            field = value
            view?.setState(value)
        }

    init {
        graphData = GraphData()
        getGraphData()
    }

    fun getGraphData() {
        viewState = TopicsListView.State.Loading
        compositeDisposable.add(graphService
                .getPosts()
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ data ->
                    graphData = data
                    addDataToGraph(graphData)
                    view?.showGraphData(graphData)
                    viewState = TopicsListView.State.Success
                }, {
                    viewState = TopicsListView.State.NetworkError
                }))
    }

    private fun addDataToGraph(graphData: GraphData) {
        for (topic in graphData.topics) {
            graph.createVertex(topic.id, topic.title)
            if (topic.requiredFor != null)
                graph.addEdge(topic.id, topic.requiredFor)
        }
        for (maps in graphData.topicsMap) {
            graph[maps.id]?.graphLessons?.addAll(maps.graphLessons)
        }
    }

    override fun attachView(view: TopicsListView) {
        super.attachView(view)
        view.setState(viewState)
        if (graphData.topics.isNotEmpty())
            view.showGraphData(graphData)
    }

    override fun destroy() {
        compositeDisposable.clear()
    }
}