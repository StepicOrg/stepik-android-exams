package org.stepik.android.exams.api.profile.model

import com.google.gson.annotations.SerializedName

class ChangePasswordRequest(
        @SerializedName("current_password")
        val currentPassword: String,
        @SerializedName("new_password")
        val newPassword: String
)