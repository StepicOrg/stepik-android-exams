package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Maybe
import io.reactivex.Single
import org.stepik.android.exams.data.db.data.TopicInfo

@Dao
interface TopicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCourseInfo(topicInfo: List<TopicInfo>)
    @Query("SELECT isJoined FROM TopicInfo")
    fun isJoinedToCourses() : Single<Boolean>
    @Query("SELECT lesson FROM TopicInfo WHERE topicId = :topicId AND type = :type")
    fun getTopicInfoByType(topicId : String, type : String) : Maybe<List<Long>>
}