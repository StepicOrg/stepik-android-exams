package org.stepik.android.exams.api.auth

import io.reactivex.Completable
import io.reactivex.Single
import org.stepik.android.exams.api.UserRegistrationRequest
import org.stepik.android.exams.configuration.Config
import org.stepik.android.exams.data.model.RegistrationUser
import org.stepik.android.exams.data.preference.AuthPreferences
import org.stepik.android.exams.di.AppSingleton
import org.stepik.android.exams.di.qualifiers.AuthLock
import org.stepik.android.exams.di.qualifiers.AuthService
import org.stepik.android.exams.di.qualifiers.CookieAuthService
import org.stepik.android.exams.di.qualifiers.SocialAuthService
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import kotlin.concurrent.withLock

@AppSingleton
class AuthRepositoryImpl
@Inject
constructor(
        @AuthLock
        private val authLock: ReentrantLock,
        @AuthService
        private val authService: OAuthService,
        @SocialAuthService
        private val socialAuthService: OAuthService,
        @CookieAuthService
        private val cookieAuthService: OAuthService,
        private val config: Config,
        private val authPreferences: AuthPreferences
): AuthRepository {

    private fun saveResponse(response: OAuthResponse, isSocial: Boolean) = authLock.withLock {
        authPreferences.oAuthResponse = response
        authPreferences.isAuthTokenSocial = isSocial
    }

    override fun authWithLoginPassword(login: String, password: String): Single<OAuthResponse> = authService
            .authWithLoginPassword(config.grantType, login, password)
            .doOnSuccess { saveResponse(it, isSocial = false) }
            .doOnError { }

    override fun authWithNativeCode(code: String, type: SocialManager.SocialType): Single<OAuthResponse> {
        var codeType: String? = null
        if (type.needUseAccessTokenInsteadOfCode()) {
            codeType = "access_token"
        }

        return socialAuthService.getTokenByNativeCode(
                type.identifier,
                code,
                config.grantTypeSocial,
                config.redirectUri,
                codeType)
                .doOnSuccess { saveResponse(it, isSocial = true) }
    }

    override fun authWithCode(code: String): Single<OAuthResponse> = socialAuthService
            .getTokenByCode(config.grantTypeSocial, code, config.redirectUri)
            .doOnSuccess { saveResponse(it, isSocial = true) }

    override fun createAccount(credentials: RegistrationUser): Completable =
            cookieAuthService.createAccount(UserRegistrationRequest(credentials))
}