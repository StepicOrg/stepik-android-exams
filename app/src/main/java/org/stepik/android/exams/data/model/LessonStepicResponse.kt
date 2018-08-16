package org.stepik.android.exams.data.model

import org.stepik.android.model.Lesson
import org.stepik.android.model.Meta

class LessonStepicResponse(
        meta: Meta?,
        var lessons: List<Lesson>? = null
) : MetaResponseBase(meta)
