package org.stepik.android.exams.data.preference

import org.stepik.android.exams.api.auth.OAuthResponse


interface AuthPreferences {
    var oAuthResponse: OAuthResponse?
    val authResponseDeadline: Long

    fun resetAuthResponseDeadline()
}