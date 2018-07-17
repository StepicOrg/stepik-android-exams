package org.stepik.android.exams.api.profile.model

import com.google.gson.annotations.SerializedName

class EditNameError(
        @SerializedName("first_name")
        var firstName: Array<String>?,

        @SerializedName("last_name")
        var lastName: Array<String>?
)