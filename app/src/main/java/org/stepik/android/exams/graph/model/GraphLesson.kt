package org.stepik.android.exams.graph.model

import com.google.gson.annotations.SerializedName

class GraphLesson(
        @SerializedName("id")
        val id: Long,
        @SerializedName("type")
        val type: Type,
        @SerializedName("description")
        val description : String,
        @SerializedName("course")
        val course: Long
) {
    enum class Type {
        @SerializedName("theory")
        THEORY,
        @SerializedName("practice")
        PRACTICE
    }
}