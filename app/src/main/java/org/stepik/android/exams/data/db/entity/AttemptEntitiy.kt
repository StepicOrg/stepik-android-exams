package org.stepik.android.exams.data.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.stepik.android.model.attempts.Dataset
import org.stepik.android.model.attempts.DatasetWrapper
@Entity
class AttemptEntitiy(
        @PrimaryKey(autoGenerate = false)
        val id: Long = 0,
        val step: Long = 0,
        val user: Long = 0,

        @SerializedName("dataset")
        private val _dataset: DatasetWrapper? = null,
        @SerializedName("dataset_url")
        val datasetUrl: String? = null,

        val status: String? = null,
        val time: String? = null,

        @SerializedName("time_left")
        val timeLeft: String? = null
) {
    val dataset: Dataset?
        get() = _dataset?.dataset
}