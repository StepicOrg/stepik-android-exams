package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single
import org.stepik.android.exams.data.db.entity.StepEntity
import org.stepik.android.model.Step

@Dao
interface StepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSteps(steps: List<StepEntity>)

    @Query("SELECT * FROM StepEntity LEFT JOIN TopicEntity WHERE StepEntity.lesson = TopicEntity.lesson AND topicId = :topicId")
    fun loadStepsByTopicId(topicId : String) : Single<List<StepEntity>>
}