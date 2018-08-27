package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.*
import org.stepik.android.exams.data.db.entity.AttemptEntitiy

interface AttemptEntitiyDao {
    @Query("SELECT * FROM AttemptEntitiy WHERE id = :id")
    fun findAttemptById(id: Long): AttemptEntitiy

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttempt(attemptEntitiy: AttemptEntitiy)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAttempt(attemptEntitiy: AttemptEntitiy)

    @Delete
    fun deleteStep(attemptEntitiy: AttemptEntitiy)
}