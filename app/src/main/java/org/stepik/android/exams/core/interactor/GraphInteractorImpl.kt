package org.stepik.android.exams.core.interactor

import io.reactivex.Single
import org.stepik.android.exams.api.graph.GraphService
import org.stepik.android.exams.core.interactor.contacts.GraphInteractor
import org.stepik.android.exams.di.AppSingleton
import org.stepik.android.exams.graph.Graph
import org.stepik.android.exams.graph.model.GraphData
import javax.inject.Inject

@AppSingleton
class GraphInteractorImpl
@Inject
constructor(
        private val graphService: GraphService
) : GraphInteractor {
    private var graph: Graph<String> = Graph()

    override fun getTopicsList() : List<String> =
            graph.getAllTopics()

    override fun getGraphData(): Single<GraphData> =
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

    override fun hasNextTopic(topicId: String, lessonId: Long) : Boolean =
            graph[topicId]?.parent?.isEmpty() == true &&
                    graph[topicId]?.graphLessons?.last()?.id == lessonId

    override fun hasPreviousTopic(topicId: String, lessonId: Long) : Boolean =
            graph[topicId]?.children?.isEmpty() == true &&
                    graph[topicId]?.graphLessons?.first()?.id == lessonId

    override fun isLastLessonInCurrentTopic(topicId: String, lessonId: Long) : Boolean =
            graph[topicId]?.graphLessons?.last()?.id == lessonId

    override fun isFirstLessonInCurrentTopic(topicId: String, lessonId: Long) : Boolean =
            graph[topicId]?.graphLessons?.first()?.id == lessonId

    override fun getNextTopic(topicId: String) : String =
            graph[topicId]?.parent?.first()?.id ?: ""

    override fun getPreviousTopic(topicId: String): String =
            graph[topicId]?.parent?.first()?.id ?: ""
}