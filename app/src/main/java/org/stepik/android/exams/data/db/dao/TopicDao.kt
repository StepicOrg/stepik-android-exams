package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Maybe
import io.reactivex.Single
import org.stepik.android.exams.data.db.entity.GraphLessonEntity
import org.stepik.android.exams.data.db.entity.TopicInfoEntity
import org.stepik.android.exams.graph.model.GraphLesson

@Dao
interface TopicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCourseInfo(topicInfoEntity: List<TopicInfoEntity>)

    @Query("SELECT isJoined FROM TopicInfoEntity")
    fun isJoinedToCourses(): Single<Boolean>

    @Query("SELECT * FROM TopicInfoEntity WHERE TopicInfoEntity.lesson = :lesson")
    fun getGraphLessonInfoByLessonId(lesson: Long): Maybe<GraphLessonEntity>

    @Query("SELECT TopicInfoEntity.lesson FROM TopicInfoEntity WHERE topicId = :topicId AND TopicInfoEntity.type = :type")
    fun getLessonIdByTopic(topicId: String, type: GraphLesson.Type): Maybe<List<Long>>

    @Query("SELECT * FROM TopicInfoEntity WHERE topicId = :topicId AND TopicInfoEntity.type = :type")
    fun getGraphLessonInfoByTopic(topicId: String, type: GraphLesson.Type): Maybe<GraphLessonEntity>

    // TODO fix constant
    @Query("SELECT TopicInfoEntity.course FROM TopicInfoEntity WHERE topicId = :topicId AND TopicInfoEntity.type = 'PRACTICE'")
    fun getAdaptiveCourseId(topicId: String): Maybe<Long>
}