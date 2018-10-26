package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single
import org.stepik.android.exams.data.db.entity.StepEntity

@Dao
interface StepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSteps(steps: List<StepEntity>)

    @Query("SELECT * FROM StepEntity LEFT JOIN TopicInfoEntity WHERE StepEntity.lesson = TopicInfoEntity.lesson AND topicId = :topicId")
    fun getStepsByTopicId(topicId : String) : Single<List<StepEntity>>
}