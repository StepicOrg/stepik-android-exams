package org.stepik.android.exams.core.presenter

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.stepik.android.exams.App
import org.stepik.android.exams.api.auth.AuthError
import org.stepik.android.exams.api.graph.GraphService
import org.stepik.android.exams.core.presenter.contracts.StudyView
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.Graph
import org.stepik.android.exams.graph.model.GraphData
import org.stepik.android.exams.graph.model.Topic
import javax.inject.Inject

class StudyPresenter
@Inject
constructor(
        private val graphService: GraphService,
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler
) : PresenterBase<StudyView>() {
    private val compositeDisposable = CompositeDisposable()

    private var graph : Graph<String> = Graph()

    fun getGraphData() {
         compositeDisposable.add(graphService
                 .getPosts()
                 .subscribeOn(backgroundScheduler)
                 .observeOn(mainScheduler)
                 .subscribe({
                     data ->
                     addDataToGraph(data)
                     view?.loadData(data)
                 }, {}))
    }
    private fun addDataToGraph(graphData: GraphData){
        for (topic in graphData.topics) {
            graph.createVertex(topic.id)
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