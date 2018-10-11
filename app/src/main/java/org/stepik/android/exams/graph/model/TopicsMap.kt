package org.stepik.android.exams.graph.model

import com.google.gson.annotations.SerializedName

data class TopicsMap(
        @SerializedName("id")
        val id: String,
        @SerializedName("lessons")
        val graphLessons: List<GraphLesson>
)