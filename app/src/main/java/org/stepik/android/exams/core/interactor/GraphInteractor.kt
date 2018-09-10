package org.stepik.android.exams.core.interactor

import io.reactivex.Single
import org.stepik.android.exams.api.graph.GraphService
import org.stepik.android.exams.di.AppSingleton
import org.stepik.android.exams.graph.Graph
import org.stepik.android.exams.graph.model.GraphData
import javax.inject.Inject

@AppSingleton
class GraphInteractor
@Inject
constructor(
        private val graphService: GraphService
) {
    private var graph: Graph<String> = Graph()

    fun getTopicsList() =
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

    fun hasNextTopic(topicId: String, lessonId: Long) =
            graph[topicId]?.parent?.isEmpty() == true &&
                    graph[topicId]?.graphLessons?.last()?.id == lessonId

    fun hasPreviousTopic(topicId: String, lessonId: Long) =
            graph[topicId]?.children?.isEmpty() == true &&
                    graph[topicId]?.graphLessons?.first()?.id == lessonId

    fun isLastLessonInCurrentTopic(topicId: String, lessonId: Long) =
            graph[topicId]?.graphLessons?.last()?.id == lessonId

    fun isFirstLessonInCurrentTopic(topicId: String, lessonId: Long) =
            graph[topicId]?.graphLessons?.first()?.id == lessonId

    fun getNextTopic(topicId: String) =
            graph[topicId]?.parent?.first()?.id ?: ""

    fun getPreviousTopic(topicId: String) =
            graph[topicId]?.parent?.first()?.id ?: ""
}