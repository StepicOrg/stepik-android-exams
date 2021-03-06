package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.*
import io.reactivex.Maybe
import org.stepik.android.exams.data.db.entity.SubmissionEntity

@Dao
interface SubmissionEntityDao {
    @Query("SELECT * FROM SubmissionEntity where attempt = :attemptId ORDER BY time DESC")
    fun findSubmissionByAttemptId(attemptId: Long): Maybe<SubmissionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubmission(submissionEntity: SubmissionEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateSubmission(submissionEntity: SubmissionEntity)

    @Delete
    fun deleteSubmission(submissionEntity: SubmissionEntity)
}