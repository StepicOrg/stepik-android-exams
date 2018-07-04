package org.stepik.android.exams.configuration

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.stepik.android.exams.di.AppSingleton
import java.io.InputStreamReader
import javax.inject.Inject

@AppSingleton
class ConfigImpl private constructor(
        @SerializedName("COURSE_ID")               override val courseId: Long,

        @SerializedName("HOST")                    override val host: String,
        @SerializedName("RATING_HOST")             override val ratingHost: String,

        @SerializedName("OAUTH_CLIENT_ID")         override val oAuthClientId: String,
        @SerializedName("OAUTH_CLIENT_SECRET")     override val oAuthClientSecret: String,
        @SerializedName("GRANT_TYPE")              override val grantType: String,

        @SerializedName("OAUTH_CLIENT_ID_SOCIAL")  override val oAuthClientIdSocial: String,
        @SerializedName("OAUTH_CLIENT_SECRET_SOCIAL") override val oAuthClientSecretSocial: String,
        @SerializedName("GRANT_TYPE_SOCIAL")       override val grantTypeSocial: String,

        @SerializedName("REDIRECT_URI")            override val redirectUri: String,

        @SerializedName("REFRESH_GRANT_TYPE")      override val refreshGrantType: String,

        @SerializedName("GOOGLE_SERVER_CLIENT_ID") override val googleServerClientId: String,
        @SerializedName("CODE_QUERY_PARAMETER")    override val codeQueryParameter: String,
        @SerializedName("APP_PUBLIC_LICENSE_KEY")  override val appPublicLicenseKey: String,
        @SerializedName("APP_METRICA_KEY")         override val appMetricaKey: String,

        @SerializedName("IS_BOOKMARKS_SUPPORTED")  override val isBookmarksSupported: Boolean,
        @SerializedName("SHOULD_DISABLE_HARDWARE_ACCELERATION") override val shouldDisableHardwareAcceleration: Boolean
): Config {
    @AppSingleton
    class ConfigFactory
    @Inject
    constructor(private val context: Context) {
        fun create(): Config = context.assets.open("config.json").use {
            Gson().fromJson(InputStreamReader(it), ConfigImpl::class.java)
        }
    }
}