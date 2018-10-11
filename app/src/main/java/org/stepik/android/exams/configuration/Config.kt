package org.stepik.android.exams.configuration

interface Config {
    val hostJsonData: String
    val host: String
    val ratingHost: String
    val oAuthClientId: String
    val oAuthClientSecret: String
    val grantType: String
    val redirectUri: String
    val refreshGrantType: String
    val codeQueryParameter: String
    val appPublicLicenseKey: String
    val amplitudeApiKey : String
}