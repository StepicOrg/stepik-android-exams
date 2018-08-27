package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.*
import org.stepik.android.exams.data.db.entity.SubmissionEntity

interface SubmissionEntityDao {
    @Query("SELECT * FROM SubmissionEntity WHERE id = :id")
    fun findAttemptById(id: Long): SubmissionEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttempt(submissionEntity: SubmissionEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAttempt(submissionEntity: SubmissionEntity)

    @Delete
    fun deleteStep(submissionEntity: SubmissionEntity)
}