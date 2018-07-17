package org.stepik.android.exams.api.profile

import io.reactivex.Completable
import io.reactivex.Single
import org.stepik.android.exams.data.model.Profile

interface ProfileRepository {
    fun fetchProfile(): Single<Profile>
    fun fetchProfileWithEmailAddresses(): Single<Profile>
    fun updateProfile(profile: Profile): Completable

    fun updatePassword(profileId: Long, oldPassword: String, newPassword: String): Completable
    fun updateEmail(newEmail: String): Completable
}