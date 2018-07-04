package org.stepik.android.exams.data.preference

import io.reactivex.Single
import org.stepik.android.exams.data.model.AccountCredentials
import org.stepik.android.exams.data.model.Profile

interface ProfilePreferences {
    val profileId: Long
    var profile: Profile?

    var fakeUser: AccountCredentials?
    fun isFakeUser(): Single<Boolean>
    fun removeFakeUser()
}