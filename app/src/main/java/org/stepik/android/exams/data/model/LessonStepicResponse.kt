package org.stepik.android.exams.data.model

class LessonStepicResponse(
        meta: Meta?,
        var lessons: List<Lesson>? = null
) : MetaResponseBase(meta)
