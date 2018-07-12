package org.stepik.android.exams.data.model


data class Block (
    var name: String? = null,
    var text: String? = null,
    var video: Video? = null ,
    var cachedLocalVideo: Video? = null
)
