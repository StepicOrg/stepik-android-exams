package org.stepik.android.exams.data.repository

import io.reactivex.Completable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.api.graph.GraphService
import org.stepik.android.exams.data.db.dao.TopicDao
import org.stepik.android.exams.data.db.data.TopicInfo
import org.stepik.android.exams.graph.Graph
import org.stepik.android.exams.graph.model.GraphData
import org.stepik.android.exams.graph.model.GraphLesson
import javax.inject.Inject

class TopicsRepository
@Inject
constructor(
        private val api: Api,
        private val graph: Graph<String>,
        private val graphService: GraphService,
        private val topicDao: TopicDao
) {
    fun getGraphData() =
            graphService.getPosts()
                    .doOnSuccess { graphData ->
                        addDataToGraph(graphData)
                    }

    fun checkIfJoinedCourse(graphData: GraphData) =
            checkIfJoined().switchIfEmpty(saveAndJoin(graphData).toMaybe())

    fun getTopicsList() =
            graph.getAllTopics()

    private fun saveAndJoin(graphData: GraphData): Completable {
        val topicsList = graphData.topicsMap.map { it.id }
        val lessonsList = graphData.topicsMap.map { it.graphLessons.map { it.id }.toLongArray() }
        val courseList = graphData.topicsMap.map { it.graphLessons.map { it.course } }
        val typesList = graphData.topicsMap.map { it.graphLessons.map { it.type } }
        return saveTopicInfoToDb(topicsList, lessonsList, typesList, courseList)
                .andThen(joinAllCourses(courseList.flatMap { it }))
    }

    private fun tryJoinCourse(lessons: Set<Long>) =
            Completable.concat(lessons.map { checkIfJoinedCourse(it) })

    private fun checkIfJoinedCourse(id: Long) =
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

    private fun checkIfJoined() =
            topicDao.isJoinedToCourses()

    private fun saveTopicInfoToDb(topics: List<String>, lessonsList: List<LongArray>, typesList: List<List<GraphLesson.Type>>, courseList: List<List<Long>>): Completable {
        val list = mutableListOf<TopicInfo>()

        for (m in 0..minOf(lessonsList.size - 1, typesList.size - 1))
            for (k in 0..minOf(lessonsList[m].size - 1, typesList[m].size - 1))
                list.add(TopicInfo(topics[m], typesList[m][k], lessonsList[m][k], courseList[m][k], true))

        return Completable.fromAction { topicDao.insertCourseInfo(list) }
    }

    private fun joinAllCourses(lessons: List<Long>) =
            tryJoinCourse(lessons.toMutableSet())
}