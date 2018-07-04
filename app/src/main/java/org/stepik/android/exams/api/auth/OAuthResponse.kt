package org.stepik.android.exams.api.auth

import com.google.gson.annotations.SerializedName

class OAuthResponse(
        @SerializedName("refresh_token")
        val refreshToken: String,
        @SerializedName("expires_in")
        val expiresIn: Long,
        val scope: String,
        @SerializedName("access_token")
        val accessToken: String,
        @SerializedName("token_type")
        val tokenType: String,
        val error: String? = null,
        @SerializedName("error_description")
        val errorDescription: String? = null
)