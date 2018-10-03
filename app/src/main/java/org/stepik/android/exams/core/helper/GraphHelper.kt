package org.stepik.android.exams.core.helper

import io.reactivex.Single
import org.stepik.android.exams.api.graph.GraphService
import org.stepik.android.exams.di.AppSingleton
import org.stepik.android.exams.graph.Graph
import org.stepik.android.exams.graph.model.GraphData
import org.stepik.android.exams.graph.model.GraphLesson
import javax.inject.Inject

@AppSingleton
class GraphHelper
@Inject
constructor(
        private val graphService: GraphService
) {
    private var graph: Graph<String> = Graph()

    fun getTopicsList() =
            graph.getAllKeys()

    fun getGraphData(): Single<GraphData> =
            graphService.getPosts()
                    .doOnSuccess { graphData ->
                        addDataToGraph(graphData)
                    }

    private fun addDataToGraph(graphData: GraphData) {
        for (topic in graphData.topics) {
            graph.createVertex(topic.id, topic.title)
        }
        graphData.topics.forEachIndexed { index, topic ->
            if (index + 1 < graphData.topics.size) {
                graph.addEdge(topic.id, graphData.topics[index + 1].id)
            }
        }
        graph.topologicalSort()
        for (maps in graphData.topicsMap) {
            graph[maps.id]?.graphLessons?.addAll(maps.graphLessons)
        }
    }

    fun hasNextTopic(topicId: String) : Boolean =
            graph[topicId]?.children?.isEmpty() == false

    fun hasPreviousTopic(topicId: String) : Boolean =
            graph[topicId]?.parent?.isEmpty() == false

    fun isLastLessonInCurrentTopic(topicId: String, lessonId: Long) : Boolean =
            graph[topicId]?.graphLessons?.last { it.type == GraphLesson.Type.THEORY }?.id == lessonId

    fun isFirstLessonInCurrentTopic(topicId: String, lessonId: Long) : Boolean =
            graph[topicId]?.graphLessons?.first()?.id == lessonId

    fun getNextTopic(topicId: String) : String =
            graph[topicId]?.children?.first()?.id ?: ""

    fun getPreviousTopic(topicId: String) :String =
            graph[topicId]?.parent?.first()?.id ?: ""
}