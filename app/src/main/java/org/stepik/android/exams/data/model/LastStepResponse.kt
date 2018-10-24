package org.stepik.android.exams.data.model

import com.google.gson.annotations.SerializedName
import org.stepik.android.model.Meta

class LastStepResponse(
        val meta: Meta?,
        @SerializedName("last-steps")
        val lastSteps: List<LastStep>
)