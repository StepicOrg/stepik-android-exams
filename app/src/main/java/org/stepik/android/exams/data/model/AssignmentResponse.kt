package org.stepik.android.exams.data.model

import org.stepik.android.model.Assignment
import org.stepik.android.model.Meta

class AssignmentResponse(meta: Meta, val assignments: List<Assignment>) : MetaResponseBase(meta)