package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.*
import org.stepik.android.exams.data.db.entity.SubmissionEntity

interface SubmissionEntityDao {
    @Query("SELECT * FROM SubmissionEntity WHERE id = :id")
    fun findSubmissionById(id: Long): SubmissionEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubmission(submissionEntity: SubmissionEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateSubmission(submissionEntity: SubmissionEntity)

    @Delete
    fun deleteSubmission(submissionEntity: SubmissionEntity)
}