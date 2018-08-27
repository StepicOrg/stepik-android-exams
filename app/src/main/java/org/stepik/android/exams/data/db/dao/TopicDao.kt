package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Maybe
import io.reactivex.Single
import org.stepik.android.exams.data.db.data.TopicInfo
import org.stepik.android.exams.graph.model.GraphLesson

@Dao
interface TopicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCourseInfo(topicInfo: List<TopicInfo>)

    @Query("SELECT isJoined FROM TopicInfo")
    fun isJoinedToCourses(): Maybe<Boolean>

    @Query("SELECT lesson FROM TopicInfo WHERE topicId = :topicId AND type = :type")
    fun getTopicInfoByType(topicId: String, type: GraphLesson.Type): Maybe<List<Long>>

    // TODO fix constant
    @Query("SELECT course FROM TopicInfo WHERE topicId = :topicId AND type = 'PRACTICE'")
    fun getAdaptiveCourseId(topicId: String): Single<Long>
}