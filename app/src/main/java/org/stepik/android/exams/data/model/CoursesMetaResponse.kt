package org.stepik.android.exams.data.model

import org.stepik.android.model.Course
import org.stepik.android.model.Meta

class CoursesMetaResponse(val courses: List<Course>, meta: Meta) : MetaResponseBase(meta)