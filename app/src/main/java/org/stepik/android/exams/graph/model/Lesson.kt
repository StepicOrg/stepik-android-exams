package org.stepik.android.exams.graph.model

import com.google.gson.annotations.SerializedName

class Lesson(
        @SerializedName("id")
        val id: Int,
        @SerializedName("type")
        val type: String
)