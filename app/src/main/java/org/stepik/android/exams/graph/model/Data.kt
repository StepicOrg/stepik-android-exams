package org.stepik.android.exams.graph.model

import com.google.gson.annotations.SerializedName

class Data(
        @SerializedName("goals")
        private val goals : List<Goal>,
        @SerializedName("topics")
        private val topics : List<Topic>,
        @SerializedName("topics-map")
        private val topicsMap : List<TopicsMap>
)