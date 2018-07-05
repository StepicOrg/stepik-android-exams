package org.stepik.android.exams.data.model

import com.google.gson.annotations.SerializedName

class Meta(
        val page: Int,
        @SerializedName("has_next")     val hasNext: Boolean,
        @SerializedName("has_previous") val hasPrevious: Boolean
)