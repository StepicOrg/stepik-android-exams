package org.stepik.android.exams.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.data.db.dao.AssignmentDao
import org.stepik.android.exams.data.db.mapping.toEntity
import org.stepik.android.model.Assignment
import org.stepik.android.model.Unit
import javax.inject.Inject

class AssignmentRepository
@Inject
constructor(
        private val assignmentDao: AssignmentDao,
        private val service : StepicRestService
) {
    fun getStepAssignment(stepId: Long, unitId: Long) : Single<Long> =
            assignmentDao.getAssignmentByStepAndUnitIds(stepId, unitId)
                    .onErrorReturn { 0 }

    fun insertAssignments(assignments : List<Assignment>) : Completable  =
            Completable.fromCallable { assignmentDao.insertAssignments(assignments.map { it.toEntity() }) }

    fun getAssignmentApi(unit : Unit): Single<List<Assignment>> =
            service.getAssignments(unit.assignments!!).map { it.assignments }
}