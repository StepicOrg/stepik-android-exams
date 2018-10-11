package org.stepik.android.exams.api.auth

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.stepik.android.exams.configuration.Config
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.data.preference.AuthPreferences
import org.stepik.android.exams.di.AppSingleton
import org.stepik.android.exams.di.qualifiers.AuthLock
import org.stepik.android.exams.di.qualifiers.AuthService
import org.stepik.android.exams.util.AppConstants
import org.stepik.android.exams.util.addUserAgent
import retrofit2.Call
import java.io.IOException
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import javax.inject.Named

@AppSingleton
class AuthInterceptor
@Inject
constructor(
        @Named(AppConstants.userAgentName)
        private val userAgent: String,

        @AuthLock
        private val authLock: ReentrantLock,

        @AuthService
        private val authService: OAuthService,
        private val config: Config,
        private val authPreferences: AuthPreferences,
        private val screenManager: ScreenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.addUserAgent(userAgent)
        var response = addAuthHeaderAndProceed(chain, request)
        if (response.code() == 400) { // was bug when user has incorrect token deadline due to wrong datetime had been set on phone
            authPreferences.resetAuthResponseDeadline()
            response = addAuthHeaderAndProceed(chain, request)
        }

        return response
    }

    private fun addAuthHeaderAndProceed(chain: Interceptor.Chain, req: Request): okhttp3.Response {
        var request = req
        try {
            authLock.lock()
            var response = authPreferences.oAuthResponse

            if (response != null) {
                if (isUpdateNeeded()) {
                    val oAuthResponse: retrofit2.Response<OAuthResponse>
                    try {
                        oAuthResponse = authWithRefreshToken(response.refreshToken).execute()
                        response = oAuthResponse.body()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        return chain.proceed(request)
                    }

                    if (response == null || !oAuthResponse.isSuccessful) {
                        if (oAuthResponse.code() == 401) {
                            screenManager.showOnboardingScreen()
                        }
                        return chain.proceed(request)
                    }

                    authPreferences.oAuthResponse = response
                }
                request = request.newBuilder()
                        .addHeader(AppConstants.authorizationHeaderName, response.tokenType + " " + response.accessToken)
                        .build()
            }
        } finally {
            authLock.unlock()
        }

        return chain.proceed(request)
    }

    private fun isUpdateNeeded() =
            DateTime.now(DateTimeZone.UTC).millis > authPreferences.authResponseDeadline

    private fun authWithRefreshToken(refreshToken: String): Call<OAuthResponse> =
            authService.refreshAccessToken(config.refreshGrantType, refreshToken)

}