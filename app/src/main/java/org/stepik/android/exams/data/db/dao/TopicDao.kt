package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Maybe
import io.reactivex.Single
import org.stepik.android.exams.data.db.entity.TopicEntity
import org.stepik.android.exams.graph.model.GraphLesson

@Dao
interface TopicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCourseInfo(topicEntity: List<TopicEntity>)

    @Query("SELECT isJoined FROM TopicEntity")
    fun isJoinedToCourses(): Single<Boolean>

    @Query("SELECT course FROM TopicEntity WHERE lesson = :lesson")
    fun getCourseInfoByLessonId(lesson: Long): Maybe<Long>

    @Query("SELECT lesson FROM TopicEntity WHERE topicId = :topicId AND type = :type")
    fun getTopicByType(topicId: String, type: GraphLesson.Type): Maybe<List<Long>>

    @Query("SELECT course FROM TopicInfo WHERE lesson = :lesson")
    fun getCourseInfoByLessonId(lesson: Long): Maybe<Long>

    // TODO fix constant
    @Query("SELECT course FROM TopicEntity WHERE topicId = :topicId AND type = 'PRACTICE'")
    fun getAdaptiveCourseId(topicId: String): Maybe<Long>
}