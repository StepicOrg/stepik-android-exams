package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import org.stepik.android.exams.data.db.data.StepInfo

@Dao
interface StepDao {
    @Query("SELECT * FROM StepInfo WHERE id = :id")
    fun findStepById(id: Long): StepInfo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStep(stepInfo: StepInfo)

    @Update(onConflict = REPLACE)
    fun updateStep(stepInfo: StepInfo)

    @Delete
    fun deleteStep(stepInfo: StepInfo)
}