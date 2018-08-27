package org.stepik.android.exams.data.repository

import org.stepik.android.exams.data.db.dao.AttemptEntityDao
import javax.inject.Inject

class AttemptRepository
@Inject
constructor(
        private val attemptEntityDao: AttemptEntityDao
) {

}