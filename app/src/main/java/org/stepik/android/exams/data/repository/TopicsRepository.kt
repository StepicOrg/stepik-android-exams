package org.stepik.android.exams.data.repository

import io.reactivex.Completable
import io.reactivex.Single
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
        private val topicDao: TopicDao
) {

    fun joinCourse(graphData: GraphData): Single<Boolean> =
            checkIfJoined().switchIfEmpty(saveAndJoin(graphData).toSingleDefault(false))

    private fun saveAndJoin(graphData: GraphData): Completable {
        val topicsList = graphData.topicsMap.map { it.id }
        val lessonsList = graphData.topicsMap.map { it.graphLessons.map { it.id }.toLongArray() }
        val courseList = graphData.topicsMap.map { it.graphLessons.map { it.course } }
        val typesList = graphData.topicsMap.map { it.graphLessons.map { it.type } }
        return joinAllCourses(courseList.flatMap { it })
                .andThen(saveTopicInfoToDb(topicsList, lessonsList, typesList, courseList))
    }

    private fun tryJoinCourse(lessons: Set<Long>) =
            Completable.concat(lessons.map { joinCourse(it) })

    private fun joinCourse(id: Long) =
            api.joinCourse(id)

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