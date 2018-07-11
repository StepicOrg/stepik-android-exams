package org.stepik.android.exams.graph.model
import com.google.gson.annotations.SerializedName
class TopicsMap (
        @SerializedName("id")
        private val id : String,
        @SerializedName("lessons")
        private val lessons : List<Lesson>
)