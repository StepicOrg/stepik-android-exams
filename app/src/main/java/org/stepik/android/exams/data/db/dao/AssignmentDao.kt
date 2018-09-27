package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single
import org.stepik.android.exams.data.db.entity.AssignmentEntity

@Dao
interface AssignmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAssignments(list : List<AssignmentEntity>)

    @Query("SELECT id FROM AssignmentEntity WHERE step =:step AND unit =:unit")
    fun getAssignmentByStepAndUnitIds(step : Long, unit : Long) : Single<Long>
}