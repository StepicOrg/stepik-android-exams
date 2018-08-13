package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import io.reactivex.Maybe
import io.reactivex.Observable
import org.stepik.android.exams.data.db.data.StepInfo

@Dao
interface StepDao {
    @Query("SELECT * FROM StepInfo WHERE id = :id")
    fun findStepById(id: Long): StepInfo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStep(stepInfo: StepInfo)

    @Update(onConflict = REPLACE)
    fun updateStep(stepInfo: StepInfo)

    @Query("UPDATE StepInfo SET isPassed = :progress WHERE id = :id")
    fun updateStepProgress(id :Long, progress: Boolean)

    @Delete
    fun deleteStep(stepInfo: StepInfo)
}