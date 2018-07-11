package org.stepik.android.exams.graph.model

import com.google.gson.annotations.SerializedName

data class GraphData(
        @SerializedName("goals")
        val goals : List<Goal>,
        @SerializedName("topics")
        val topics : List<Topic>,
        @SerializedName("topics-map")
        val topicsMap : List<TopicsMap>
)