package org.stepik.android.exams.configuration

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import org.stepik.android.exams.di.AppSingleton
import java.io.InputStreamReader
import javax.inject.Inject

@AppSingleton
class ConfigImpl private constructor(
        override val hostJsonData: String,
        override val host: String,
        override val ratingHost: String,
        override val oAuthClientId: String,
        override val oAuthClientSecret: String,
        override val grantType: String,
        override val redirectUri: String,
        override val refreshGrantType: String,
        override val codeQueryParameter: String,
        override val appPublicLicenseKey: String,
        override val amplitudeApiKey: String
) : Config {
    @AppSingleton
    class ConfigFactory
    @Inject
    constructor(private val context: Context) {
        fun create(): Config = context.assets.open("config.json").use {
            GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create().fromJson(InputStreamReader(it), ConfigImpl::class.java)
        }
    }
}