package org.stepik.android.exams.graph.model
import com.google.gson.annotations.SerializedName
class Topic (
        @SerializedName("id")
        private val id : String,
        @SerializedName("title")
        private val title : String,
        @SerializedName("required-for")
        private val requiredFor :String
)