package org.stepik.android.exams.configuration

interface Config {
    val courseId: Long

    val host: String
    val ratingHost: String
    val oAuthClientId: String
    val oAuthClientSecret: String
    val grantType: String
    val oAuthClientIdSocial: String
    val oAuthClientSecretSocial: String
    val grantTypeSocial: String
    val redirectUri: String
    val refreshGrantType: String
    val codeQueryParameter: String
    val appPublicLicenseKey: String
    val shouldDisableHardwareAcceleration: Boolean
}