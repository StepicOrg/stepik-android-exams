package org.stepik.android.exams.core.interactor.contacts

import io.reactivex.Single
import org.stepik.android.exams.graph.model.GraphData

interface GraphInteractor {
    fun getTopicsList() : List<String>
    fun getGraphData() : Single<GraphData>
    fun hasNextTopic(topicId: String, lessonId: Long) : Boolean
    fun hasPreviousTopic(topicId: String, lessonId: Long) : Boolean
    fun isLastLessonInCurrentTopic(topicId: String, lessonId: Long) : Boolean
    fun isFirstLessonInCurrentTopic(topicId: String, lessonId: Long) : Boolean
    fun getNextTopic(topicId: String) : String
    fun getPreviousTopic(topicId: String) : String
}