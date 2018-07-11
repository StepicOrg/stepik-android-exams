package org.stepik.android.exams.graph.model

import com.google.gson.annotations.SerializedName

class Lesson (
        @SerializedName("id")
        private val id: Int,
        @SerializedName("type")
        private val type: String
)