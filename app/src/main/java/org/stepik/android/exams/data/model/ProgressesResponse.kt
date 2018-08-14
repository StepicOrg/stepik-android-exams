package org.stepik.android.exams.data.model

class ProgressesResponse(meta: Meta, progresses: List<Progress>) : MetaResponseBase(meta) {

    var progresses: List<Progress>
        internal set

    init {
        this.progresses = progresses
    }
}