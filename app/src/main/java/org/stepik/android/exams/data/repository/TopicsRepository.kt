package org.stepik.android.exams.data.repository

import io.reactivex.Completable
import io.reactivex.Maybe
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.data.db.dao.TopicDao
import org.stepik.android.exams.data.db.data.TopicInfo
import org.stepik.android.exams.graph.model.GraphData
import org.stepik.android.exams.graph.model.GraphLesson
import javax.inject.Inject

class TopicsRepository
@Inject
constructor(
        private val api: Api,
        private val topicDao: TopicDao
) {
    data class TopicsData(val topics: List<String>, val lessonsList: List<LongArray>, val typesList: List<List<GraphLesson.Type>>, val courseList: List<List<Long>>)

    fun joinCourse(graphData: GraphData): Maybe<Boolean> =
            checkIfJoined()
                    .switchIfEmpty(
                            Maybe.just(parseTopicsData(graphData)).flatMapCompletable { topicsData ->
                                val courseList = topicsData.courseList
                                joinAllCourses(courseList.flatMap { it })
                                        .andThen(saveTopicInfoToDb(topicsData))
                            }
                                    .toSingleDefault(true)
                                    .toMaybe())

    private fun parseTopicsData(graphData: GraphData): TopicsData {
        val topicsList = graphData.topicsMap.map { it.id }
        val lessonsList = graphData.topicsMap.map { it.graphLessons.map { it.id }.toLongArray() }
        val courseList = graphData.topicsMap.map { it.graphLessons.map { it.course } }
        val typesList = graphData.topicsMap.map { it.graphLessons.map { it.type } }
        return TopicsData(topicsList, lessonsList, typesList, courseList)
    }

    private fun joinAllCourses(lessons: List<Long>) =
            Completable.concat(lessons.toMutableSet().map { joinCourse(it) })

    private fun joinCourse(id: Long) =
            api.joinCourse(id)

    private fun checkIfJoined() =
            topicDao.isJoinedToCourses()

    private fun saveTopicInfoToDb(topicsData: TopicsData): Completable {
        val list = mutableListOf<TopicInfo>()
        val (topics, lessonsList, typesList, courseList) = topicsData

        for (m in 0..minOf(lessonsList.size - 1, typesList.size - 1))
            for (k in 0..minOf(lessonsList[m].size - 1, typesList[m].size - 1))
                list.add(TopicInfo(topics[m], typesList[m][k], lessonsList[m][k], courseList[m][k], true))

        return Completable.fromAction { topicDao.insertCourseInfo(list) }
    }
}