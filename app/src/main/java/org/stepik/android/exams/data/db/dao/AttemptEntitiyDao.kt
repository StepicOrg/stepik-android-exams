package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.*
import io.reactivex.Completable
import io.reactivex.Maybe
import org.stepik.android.exams.data.db.entity.AttemptEntitiy

interface AttemptEntitiyDao {
    @Query("SELECT * FROM AttemptEntitiy WHERE id = :id")
    fun findAttemptById(id: Long): Maybe<AttemptEntitiy>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttempt(attemptEntitiy: AttemptEntitiy) : Completable

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAttempt(attemptEntitiy: AttemptEntitiy) : Completable

    @Delete
    fun deleteAttempt(attemptEntitiy: AttemptEntitiy)
}