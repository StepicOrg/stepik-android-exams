package org.stepik.android.exams.graph.model

import com.google.gson.annotations.SerializedName

class Goal (
        @SerializedName("title")
        private val title: String,
        @SerializedName("id")
        private val id : String,
        @SerializedName("required-topics")
        private val requiredTopics : List<String>
)