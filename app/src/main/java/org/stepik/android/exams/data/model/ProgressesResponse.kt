package org.stepik.android.exams.data.model

import org.stepik.android.model.Meta
import org.stepik.android.model.Progress

class ProgressesResponse(meta: Meta, progresses: List<Progress>) : MetaResponseBase(meta) {

    var progresses: List<Progress>
        internal set

    init {
        this.progresses = progresses
    }
}
