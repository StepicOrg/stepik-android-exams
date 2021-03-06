package org.stepik.android.exams.data.model

import org.stepik.android.model.Meta
import org.stepik.android.model.Progress

class ProgressesResponse(
        meta: Meta,
        val progresses: List<Progress>
) : MetaResponseBase(meta)
