package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single
import org.stepik.android.exams.data.db.entity.StepEntity

@Dao
interface StepDao {
    @Query("SELECT * FROM StepEntity WHERE id = :id")
    fun findStepById(id: Long): StepEntity

    @Query("SELECT ProgressEntity.progress FROM StepEntity JOIN TopicInfo ON StepEntity.lesson = TopicInfo.lesson JOIN ProgressEntity ON StepEntity.id = ProgressEntity.stepId WHERE TopicInfo.topicId = :topicId")
    fun getAllStepsProgressByTopicId(topicId: String) : Single<List<String>>

    @Query("SELECT ProgressEntity.isPassed FROM StepEntity JOIN TopicInfo ON StepEntity.lesson = TopicInfo.lesson JOIN ProgressEntity ON StepEntity.id = ProgressEntity.stepId WHERE TopicInfo.topicId = :topicId")
    fun findPassedStepsByTopicId(topicId: String): Single<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSteps(stepEntity: List<StepEntity>)

    @Query("SELECT isPassed FROM StepEntity JOIN ProgressEntity ON id = stepId WHERE id = :stepId AND isPassed=1")
    fun getStepProgress(stepId : Long) : Boolean

    @Query("UPDATE ProgressEntity SET isPassed =:isPassed WHERE stepId=:id")
    fun updateStepProgress(id: Long, isPassed: Boolean)
}