package org.stepik.android.exams.core.presenter

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.api.graph.GraphService
import org.stepik.android.exams.core.presenter.contracts.TopicsListView
import org.stepik.android.exams.data.db.dao.TopicDao
import org.stepik.android.exams.data.db.data.TopicInfo
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
                    checkIfJoined()
                    view?.showGraphData(graphData)
                    viewState = TopicsListView.State.Success
                }, {
                    viewState = TopicsListView.State.NetworkError
                }))
    }

    private fun tryJoinCourse(id: String, lessons : List<Long>) =
            Completable.concat(lessons.map { joinCourse(it) })


    private fun joinCourse(id: Long) =
            api.joinCourse(id)

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
    private fun checkIfJoined() {
        topicDao.isJoinedToCourses()
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({joined ->
                    if (joined)
                        return@subscribe
                }, {
                    val topicsList = graphData.topicsMap.map { it.id }
                    val lessonsList = graphData.topicsMap.map { it.graphLessons.map { it.id }.toLongArray() }
                    val courseList = graphData.topicsMap.map { it.graphLessons.map { it.course } }
                    val typesList = graphData.topicsMap.map { it.graphLessons.map { it.type } }
                    saveTopicInfoToDb(topicsList, lessonsList, typesList, courseList)
                    joinAllCourses(topicsList, courseList.flatMap { it })
                })
    }


    private fun saveTopicInfoToDb(topics: List<String>, lessonsList: List<LongArray>, typesList: List<List<String>>, courseList : List<List<Long>>) {
        val list = mutableListOf<TopicInfo>()
             (0 until topics.size).map { i ->
                 (0..minOf(lessonsList.size-1, typesList.size-1)).map { m ->
                     val size = lessonsList[m].size-1
                     (0..minOf(lessonsList[m].size-1, typesList[m].size-1)).map { k ->
                         list.add(TopicInfo(topics[i], typesList[m][k], lessonsList[m][k], courseList[m][k], true))
                     }
                 }
             }
        Completable.fromCallable { topicDao.insertCourseInfo(list) }
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe()
    }

    private fun joinAllCourses(topics: List<String>, lessons : List<Long>) {
        Completable.concat(topics.map { tryJoinCourse(it, lessons) })
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