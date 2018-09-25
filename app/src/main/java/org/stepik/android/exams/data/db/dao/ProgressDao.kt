package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import org.stepik.android.exams.data.db.entity.ProgressEntity

@Dao
interface ProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStepProgress(progressEntity: ProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStepProgress(progressEntity: List<ProgressEntity>)
}