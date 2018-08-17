package org.stepik.android.exams.data.preference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

import com.google.gson.Gson
import io.reactivex.Single
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.stepik.android.exams.api.auth.OAuthResponse
import org.stepik.android.exams.data.model.AccountCredentials
import org.stepik.android.exams.data.model.Profile
import org.stepik.android.exams.di.AppSingleton
import javax.inject.Inject

@AppSingleton
class SharedPreferenceHelper
@Inject
constructor(context: Context) : SharedPreferenceProvider, AuthPreferences, ProfilePreferences {
    override val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val gson = Gson()

    override var oAuthResponse: OAuthResponse?
        get() {
            val json = getString(OAUTH_RESPONSE) ?: return null
            return gson.fromJson(json, OAuthResponse::class.java)
        }
        set(response) {
            if (response != null) {
                val json = gson.toJson(response)

                val currentTime = DateTime.now(DateTimeZone.UTC).millis

                saveString(OAUTH_RESPONSE, json)
                authResponseDeadline = currentTime + (response.expiresIn - 50) * 1000
            }
        }

    override var authResponseDeadline: Long
        get() = getLong(OAUTH_RESPONSE_DEADLINE)
        private set(value) = saveLong(OAUTH_RESPONSE_DEADLINE, value)

    override var profile: Profile?
        get() {
            val json = getString(PROFILE) ?: return null
            return gson.fromJson(json, Profile::class.java)
        }
        set(profile) {
            if (profile != null) {
                val json = gson.toJson(profile)
                saveString(PROFILE, json)

                profileId = profile.id
            }
        }

    override var profileId: Long
        get() = getLong(PROFILE_ID)
        private set(value) = saveLong(PROFILE_ID, value)

    override var fakeUser: AccountCredentials?
        get() = getString(FAKE_USER)?.let { gson.fromJson(it, AccountCredentials::class.java) }
        set(value) {
            val json = value?.let { gson.toJson(it) }
            saveString(FAKE_USER, json)
        }

    var isNotFirstTime: Boolean by preference(NOT_FIRST_TIME)
    var isFirstTimeAdaptive: Boolean by preference(FIRST_TIME_ADAPTIVE)

    fun removeProfile() {
        remove(PROFILE)
        remove(PROFILE_ID)
        remove(OAUTH_RESPONSE)
        remove(OAUTH_RESPONSE_DEADLINE)
    }

    override fun isFakeUser(): Single<Boolean> =
            Single.fromCallable { sharedPreferences.contains(FAKE_USER) }

    override fun removeFakeUser() {
        remove(FAKE_USER)
    }

    override fun resetAuthResponseDeadline() {
        remove(OAUTH_RESPONSE_DEADLINE)
    }

    fun saveBoolean(name: String, data: Boolean) {
        sharedPreferences[name] = data
    }

    private fun saveString(name: String, data: String?) {
        sharedPreferences[name] = data
    }

    fun saveLong(name: String, data: Long) {
        sharedPreferences[name] = data
    }

    fun changeLong(name: String, delta: Long): Long {
        val value = sharedPreferences.get<Long>(name) + delta
        sharedPreferences[name] = value
        return value
    }

    private fun getString(name: String): String? = sharedPreferences[name]

    fun getLong(name: String): Long = sharedPreferences[name]

    fun getBoolean(name: String): Boolean = sharedPreferences[name]

    fun remove(name: String) {
        sharedPreferences.edit().remove(name).apply()
    }

    companion object {
        private const val OAUTH_RESPONSE = "oauth_response"
        private const val OAUTH_RESPONSE_DEADLINE = "oauth_response_deadline"
        private const val PROFILE = "profile"
        private const val PROFILE_ID = "profile_id"
        private const val NOT_FIRST_TIME = "not_first_time"
        private const val FIRST_TIME_ADAPTIVE = "first_time_adaptive"
        private const val FAKE_USER = "fake_user"
    }

}