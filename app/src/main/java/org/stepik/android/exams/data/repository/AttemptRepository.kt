package org.stepik.android.exams.data.repository

import org.stepik.android.exams.data.db.dao.AttemptEntitiyDao
import javax.inject.Inject

class AttemptRepository
@Inject
constructor(
        private val attemptEntitiyDao: AttemptEntitiyDao
) {

}