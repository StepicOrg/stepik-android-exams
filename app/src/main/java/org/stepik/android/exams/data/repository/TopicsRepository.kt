package org.stepik.android.exams.data.repository

import io.reactivex.Completable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.data.db.dao.TopicDao
import org.stepik.android.exams.data.db.entity.TopicInfoEntity
import org.stepik.android.exams.data.db.mapping.toEntity
import org.stepik.android.exams.graph.model.GraphData
import javax.inject.Inject

class TopicsRepository
@Inject
constructor(
        private val api: Api,
        private val topicDao: TopicDao
) {
    fun joinCourse(graphData: GraphData): Completable =
            topicDao.isJoinedToCourses()
                    .onErrorReturnItem(false)
                    .flatMapCompletable {
                        if (it) {
                            Completable.complete()
                        } else {
                            val topicEntity = parseData(graphData)
                            val courses = topicEntity.map { it.graphLesson.course }.distinct()
                            Completable.concat(courses.map { joinCourse(it) })
                                    .andThen(Completable.fromAction { topicDao.insertCourseInfo(topicEntity) })
                        }
                    }

    private fun joinCourse(id: Long) =
            api.joinCourse(id)

    private fun parseData(graphData: GraphData): List<TopicInfoEntity> {
        val list = mutableListOf<TopicInfoEntity>()
        val numberOfTopics = graphData.topicsMap.size
        for (m in 0 until numberOfTopics)
            for (k in 0 until graphData.topicsMap[m].graphLessons.size)
                list.add(TopicInfoEntity(
                        graphData.topics[m].toEntity(),
                        graphData.topicsMap[m].graphLessons[k].toEntity(),
                        true))
        return list
    }

}