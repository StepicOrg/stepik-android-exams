package org.stepik.android.exams.core.helper

import io.reactivex.Single
import org.stepik.android.exams.api.graph.GraphService
import org.stepik.android.exams.di.AppSingleton
import org.stepik.android.exams.graph.Graph
import org.stepik.android.exams.graph.model.GraphData
import javax.inject.Inject

@AppSingleton
class GraphHelper
@Inject
constructor(
        private val graphService: GraphService
) {
    private var graph: Graph<String> = Graph()

    fun getTopicsList() : List<String> =
            graph.getAllTopics()

    fun getGraphData(): Single<GraphData> =
            graphService.getPosts()
                    .doOnSuccess { graphData ->
                        addDataToGraph(graphData)
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

    fun hasNextTopic(topicId: String, lessonId: Long) : Boolean =
            graph[topicId]?.parent?.isEmpty() == true &&
                    graph[topicId]?.graphLessons?.last()?.id == lessonId

    fun hasPreviousTopic(topicId: String, lessonId: Long) : Boolean =
            graph[topicId]?.children?.isEmpty() == true &&
                    graph[topicId]?.graphLessons?.first()?.id == lessonId

    fun isLastLessonInCurrentTopic(topicId: String, lessonId: Long) : Boolean =
            graph[topicId]?.graphLessons?.last()?.id == lessonId

    fun isFirstLessonInCurrentTopic(topicId: String, lessonId: Long) : Boolean =
            graph[topicId]?.graphLessons?.first()?.id == lessonId

    fun getNextTopic(topicId: String) : String =
            graph[topicId]?.parent?.first()?.id ?: ""

    fun getPreviousTopic(topicId: String): String =
            graph[topicId]?.parent?.first()?.id ?: ""
}