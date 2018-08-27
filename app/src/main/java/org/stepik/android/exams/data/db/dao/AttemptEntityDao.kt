package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.*
import io.reactivex.Maybe
import org.stepik.android.exams.data.db.entity.AttemptEntity

@Dao
interface AttemptEntityDao {
    @Query("SELECT * FROM AttemptEntity WHERE id = :id")
    fun findAttemptById(id: Long): Maybe<AttemptEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttempt(attemptEntity: AttemptEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAttempt(attemptEntity: AttemptEntity)

    @Delete
    fun deleteAttempt(attemptEntity: AttemptEntity)
}