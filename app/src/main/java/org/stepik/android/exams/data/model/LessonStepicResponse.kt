package org.stepik.android.exams.data.model

class LessonStepicResponse(
        meta: Meta,
        val lessons: List<Lesson>? = null
) : MetaResponseBase(meta)
