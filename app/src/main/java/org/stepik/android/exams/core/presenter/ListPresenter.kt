package org.stepik.android.exams.core.presenter

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.api.graph.GraphService
import org.stepik.android.exams.core.presenter.contracts.ListView
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.Graph
import org.stepik.android.exams.graph.model.GraphData
import javax.inject.Inject

class ListPresenter
@Inject
constructor(
        private val graph: Graph<String>,
        private val graphService: GraphService,
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler
) : PresenterBase<ListView>() {
    private val compositeDisposable = CompositeDisposable()

    fun getGraphData() {
         compositeDisposable.add(graphService
                 .getPosts()
                 .subscribeOn(backgroundScheduler)
                 .observeOn(mainScheduler)
                 .subscribe { data ->
                     addDataToGraph(data)
                     view?.loadData(data)
                 })
    }
    private fun addDataToGraph(graphData: GraphData){
        for (topic in graphData.topics) {
            graph.createVertex(topic.id, topic.title)
            if (topic.requiredFor != null)
                graph.addEdge(topic.id, topic.requiredFor)
        }
        for (maps in graphData.topicsMap){
            graph.getVertex(maps.id)?.lessons?.addAll(maps.lessons)
        }
    }
    override fun destroy() {
        compositeDisposable.clear()
    }
}