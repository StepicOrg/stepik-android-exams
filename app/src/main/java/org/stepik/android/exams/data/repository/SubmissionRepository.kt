package org.stepik.android.exams.data.repository

import org.stepik.android.exams.data.db.dao.SubmissionEntityDao
import javax.inject.Inject

class SubmissionRepository
@Inject
constructor(
    private val submissionEntityDao: SubmissionEntityDao
) {

}