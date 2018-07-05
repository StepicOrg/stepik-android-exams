package org.stepik.android.adaptive.api.profile.model

import com.google.gson.annotations.SerializedName

class ChangePasswordRequest(
        @SerializedName("current_password")
        val currentPassword: String,
        @SerializedName("new_password")
        val newPassword: String
)