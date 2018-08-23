package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import org.stepik.android.exams.data.db.data.TopicInfo

@Dao
interface TopicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCourseInfo(topicInfo: List<TopicInfo>)
}