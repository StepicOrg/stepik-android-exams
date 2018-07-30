package org.stepik.android.exams.data.model.attempts

import com.google.gson.annotations.SerializedName
import org.stepik.android.exams.data.model.Dataset

class Attempt(
        val id: Long = 0,
        val step: Long = 0,
        val user: Long = 0,

        @SerializedName("dataset")
        val _dataset: Dataset? = null,
        @SerializedName("dataset_url")
        val datasetUrl: String? = null,

        val status: String? = null,
        val time: String? = null,

        @SerializedName("time_left")
        val timeLeft: String? = null
)