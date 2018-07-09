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
        @CookieAuthService
        private val cookieAuthService: OAuthService,
        private val config: Config,
        private val authPreferences: AuthPreferences
): AuthRepository {

    private fun saveResponse(response: OAuthResponse) = authLock.withLock {
        authPreferences.oAuthResponse = response
    }

    override fun authWithLoginPassword(login: String, password: String): Single<OAuthResponse> = authService
            .authWithLoginPassword(config.grantType, login, password)
            .doOnSuccess { saveResponse(it) }
            .doOnError {}


    override fun createAccount(credentials: RegistrationUser): Completable =
            cookieAuthService.createAccount(UserRegistrationRequest(credentials))
}