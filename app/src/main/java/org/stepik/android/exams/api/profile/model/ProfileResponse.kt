package org.stepik.android.exams.api.profile.model

import org.stepik.android.exams.data.model.Profile

class ProfileResponse(@JvmField val profiles: List<Profile>?) {
    val profile: Profile?
        get() = profiles?.firstOrNull()
}
