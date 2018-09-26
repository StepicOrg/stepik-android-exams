package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import org.stepik.android.exams.data.db.entity.StepEntity

@Dao
interface StepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSteps(steps: List<StepEntity>)
}