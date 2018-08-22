package org.stepik.android.exams.data.model


class ViewAssignmentWrapper(assignmentId: Long?, stepId: Long) {
    private val view: ViewAssignment = ViewAssignment(assignmentId, stepId)

    val step: Long
        get() = view.step

}
