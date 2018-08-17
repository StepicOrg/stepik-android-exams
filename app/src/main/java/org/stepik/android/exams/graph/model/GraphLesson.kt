package org.stepik.android.exams.graph.model

import com.google.gson.annotations.SerializedName

class GraphLesson(
        @SerializedName("id")
        val id: Int,
        @SerializedName("type")
        val type: String,
        @SerializedName("course")
        val course: Long
)