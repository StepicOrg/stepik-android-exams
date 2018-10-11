package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single
import org.stepik.android.exams.data.db.entity.ProgressEntity

@Dao
interface ProgressDao {
    @Query("SELECT progress FROM ProgressEntity JOIN TopicInfoEntity ON ProgressEntity.lessonId = TopicInfoEntity.lesson WHERE TopicInfoEntity.topicId = :topicId")
    fun getAllStepsProgressByTopicId(topicId: String) : Single<List<String>>

    @Query("SELECT isPassed FROM ProgressEntity JOIN TopicInfoEntity ON ProgressEntity.lessonId = TopicInfoEntity.lesson WHERE TopicInfoEntity.topicId = :topicId")
    fun getPassedStepsByTopicId(topicId: String) : Single<List<Boolean>>

    @Query("SELECT isPassed FROM ProgressEntity WHERE stepId = :stepId")
    fun getPassedStep(stepId : Long) : Single<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStepProgress(progressEntity: ProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStepProgress(progressEntity: List<ProgressEntity>)
}