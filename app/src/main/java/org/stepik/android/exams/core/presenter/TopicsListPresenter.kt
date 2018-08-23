package org.stepik.android.exams.core.presenter

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.api.graph.GraphService
import org.stepik.android.exams.core.presenter.contracts.TopicsListView
import org.stepik.android.exams.data.db.dao.TopicDao
import org.stepik.android.exams.data.db.data.CourseInfo
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.Graph
import org.stepik.android.exams.graph.model.GraphData
import org.stepik.android.exams.graph.model.GraphLesson
import org.stepik.android.exams.ui.activity.TopicsListActivity
import org.stepik.android.exams.util.AppConstants
import javax.inject.Inject

class TopicsListPresenter
@Inject
constructor(
        private val api: Api,
        private val graph: Graph<String>,
        private val graphService: GraphService,
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler,
        private val topicDao: TopicDao
) : PresenterBase<TopicsListView>() {
    private val compositeDisposable = CompositeDisposable()

    private var graphData: GraphData
    private var type = TopicsListActivity.TYPE.THEORY

    private var viewState: TopicsListView.State = TopicsListView.State.Idle
        set(value) {
            field = value
            view?.setState(value)
        }

    init {
        graphData = GraphData()
        getGraphData()
    }

    fun setType(type: TopicsListActivity.TYPE) {
        this.type = type
        view?.setActivityType(type)
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

    private fun tryJoinCourse(id: String): Completable {
        val list = mutableListOf<Completable>()
        for (u in getUniqueCourses(parseLessons(id)))
            list.add(joinCourse(u))
        return Completable.concat(list)
    }

    private fun getUniqueCourses(graphLessons: List<GraphLesson>): Set<Long> {
        val uniqueCourses = mutableSetOf<Long>()
        uniqueCourses.addAll(graphLessons.map { it.course })
        return uniqueCourses
    }

    private fun joinCourse(id: Long) =
            api.joinCourse(id)

    private fun getLessonsById(id: String) = graph[id]?.graphLessons

    fun parseLessons(id: String) : List<GraphLesson> {
        val filterType =
        when (type){
            TopicsListActivity.TYPE.THEORY -> AppConstants.lessonTheory
            TopicsListActivity.TYPE.ADAPTIVE -> AppConstants.lessonPractice
        }
        return getLessonsById(id)!!.filter { it.type == filterType }
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
        val topicsList = graphData.topicsMap.map { it.id }
        val lessonsList = graphData.topicsMap.map { it.graphLessons.map { it.course }.toLongArray() }
        val typesList = graphData.topicsMap.map { it.graphLessons.map { it.type }.toString() }
        joinAllCourses(topicsList)
        saveTopicInfoToDb(topicsList, lessonsList, typesList)
    }

    private fun saveTopicInfoToDb(topics: List<String>, lessonsList : List<LongArray>, typesList : List<String>){

        topicDao.insertCourseInfo(CourseInfo())
    }

    private fun joinAllCourses(topics : List<String>){
        Completable.concat(topics.map { tryJoinCourse(it) })
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({}, {})
    }

    override fun attachView(view: TopicsListView) {
        super.attachView(view)
        view.setState(viewState)
        setType(type)
        if (graphData.topics.isNotEmpty()) {
            view.showGraphData(graphData)
        }
    }

    override fun destroy() {
        compositeDisposable.clear()
    }
}