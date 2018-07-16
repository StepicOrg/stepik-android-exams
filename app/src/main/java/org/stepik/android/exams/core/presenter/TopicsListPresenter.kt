package org.stepik.android.exams.core.presenter

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.api.Errors
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

    override fun onFirstViewAttach() = getGraphData()

    private var graphData : GraphData

    init {
        graphData = GraphData()
    }

    fun getGraphData() {
        compositeDisposable.add(graphService
                .getPosts()
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ data ->
                    graphData = data
                    addDataToGraph(graphData)
                    view?.showGraphData(graphData)
                    view?.hideRefreshView()
                }, {
                    onError(Errors.ConnectionProblem)
                }))
    }
    private fun onError(error : Errors){
        view?.onError(error)
    }

    private fun addDataToGraph(graphData: GraphData){
        for (topic in graphData.topics) {
            graph.createVertex(topic.id, topic.title)
            if (topic.requiredFor != null)
                graph.addEdge(topic.id, topic.requiredFor)
        }
        for (maps in graphData.topicsMap){
            graph[maps.id]?.lessons?.addAll(maps.lessons)
        }
    }

    override fun attachView(view: TopicsListView) {
        super.attachView(view)
        if (graphData.topics.isNotEmpty())
        view.showGraphData(graphData)
    }

    override fun destroy() {
        compositeDisposable.clear()
    }
}