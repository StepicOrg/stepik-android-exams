package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.*
import org.stepik.android.exams.data.db.entity.AttemptEntity

interface AttemptEntitiyDao {
    @Query("SELECT * FROM AttemptEntitiy WHERE id = :id")
    fun findAttemptById(id: Long): AttemptEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttempt(attemptEntitiy: AttemptEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAttempt(attemptEntitiy: AttemptEntity)

    @Delete
    fun deleteAttempt(attemptEntitiy: AttemptEntity)
}