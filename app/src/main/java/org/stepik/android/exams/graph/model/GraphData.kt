package org.stepik.android.exams.graph.model

import com.google.gson.annotations.SerializedName

data class GraphData(
        @SerializedName("goals")
        val goals : List<Goal> = listOf(),
        @SerializedName("topics")
        val topics : List<Topic> = listOf(),
        @SerializedName("topics-map")
        val topicsMap : List<TopicsMap> = listOf()
)