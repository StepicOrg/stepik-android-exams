package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import io.reactivex.Single
import org.stepik.android.exams.data.db.data.StepInfo

@Dao
interface StepDao {
    @Query("SELECT * FROM StepInfo WHERE id = :id")
    fun findStepById(id: Long): StepInfo

    @Query("SELECT COUNT(isPassed) FROM StepInfo WHERE topic = :topicId AND isPassed=1")
    fun findPassedStepsByTopicId(topicId: String): Single<Float>

    @Query("SELECT COUNT(*) FROM StepInfo WHERE topic = :topicId")
    fun findAllProgressByTopicId(topicId: String): Single<Float>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStep(stepInfo: StepInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSteps(id: List<StepInfo>)

    @Update(onConflict = REPLACE)
    fun updateStep(stepInfo: StepInfo)

    @Query("UPDATE StepInfo SET isPassed = :progress WHERE id = :id")
    fun updateStepProgress(id: Long, progress: Boolean)

    @Delete
    fun deleteStep(stepInfo: StepInfo)
}