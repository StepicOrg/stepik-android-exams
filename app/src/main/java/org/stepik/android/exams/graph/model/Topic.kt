package org.stepik.android.exams.graph.model

import com.google.gson.annotations.SerializedName

data class Topic(
        @SerializedName("id")
        val id: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("required-for")
        val requiredFor: String?
)