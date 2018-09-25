package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single
import org.stepik.android.exams.data.db.entity.ProgressEntity

@Dao
interface ProgressDao {
    @Query("SELECT ProgressEntity.progress FROM ProgressEntity JOIN TopicInfo ON ProgressEntity.lessonId = TopicInfo.lesson WHERE TopicInfo.topicId = :topicId")
    fun getAllStepsProgressByTopicId(topicId: String) : Single<List<String>>

    @Query("SELECT isPassed FROM ProgressEntity WHERE stepId = :stepId")
    fun getStepProgress(stepId : Long) : Single<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStepProgress(progressEntity: ProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStepProgress(progressEntity: List<ProgressEntity>)
}