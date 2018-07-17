package org.stepik.android.exams.graph.model

import com.google.gson.annotations.SerializedName

data class Goal(
        @SerializedName("title")
        val title: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("required-topics")
        val requiredTopics: List<String>
)