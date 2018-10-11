package org.stepik.android.adaptive.api.profile.model

class ProfileCompositeError(
        val email: List<String>?,
        val detail: String?
) {
    val asList: List<String?>
        get() = (email ?: emptyList()) + detail
}