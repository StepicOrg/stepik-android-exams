package org.stepik.android.exams.api

import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.stepik.android.exams.api.auth.CookieHelper
import org.stepik.android.exams.api.auth.EmptyAuthService
import org.stepik.android.exams.configuration.Config
import org.stepik.android.exams.data.model.AccountCredentials
import org.stepik.android.exams.data.model.EnrollmentWrapper
import org.stepik.android.exams.data.model.LessonStepicResponse
import org.stepik.android.exams.data.model.StepResponse
import org.stepik.android.exams.di.network.NetworkHelper
import org.stepik.android.exams.util.AppConstants
import org.stepik.android.exams.util.Util
import org.stepik.android.exams.util.addUserAgent
import org.stepik.android.exams.util.setTimeoutsInSeconds
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Named

class Api
@Inject
constructor(
        private val config: Config,
        @Named(AppConstants.userAgentName)
        private val userAgent: String,
        private val cookieHelper: CookieHelper,
        private val stepikService: StepikRestService
) {

    companion object {
        private const val FAKE_MAIL_PATTERN = "exams_%s_android_%d%s@stepik.org"
        private const val TIMEOUT_IN_SECONDS = 60L
    }

    fun createFakeAccount(): AccountCredentials {
        val email = String.format(FAKE_MAIL_PATTERN, 0, System.currentTimeMillis(), Util.randomString(5))
        val password = Util.randomString(16)
        val firstName = Util.randomString(10)
        val lastName = Util.randomString(10)
        return AccountCredentials(email, password, firstName, lastName)
    }

    fun remindPassword(email: String): Completable {
        val encodedEmail = URLEncoder.encode(email, Charsets.UTF_8.name())
        val interceptor = Interceptor { chain ->
            var newRequest = chain.addUserAgent(userAgent)

            val cookies = cookieHelper.getCookiesForBaseUrl()
                    ?: return@Interceptor chain.proceed(newRequest)
            var csrftoken: String? = null
            var sessionId: String? = null
            for (item in cookies) {
                if (item.name == AppConstants.csrfTokenCookieName) {
                    csrftoken = item.value
                    continue
                }
                if (item.name == AppConstants.sessionCookieName) {
                    sessionId = item.value
                }
            }

            val cookieResult = AppConstants.csrfTokenCookieName + "=" + csrftoken + "; " + AppConstants.sessionCookieName + "=" + sessionId
            if (csrftoken == null) return@Interceptor chain.proceed(newRequest)
            val url = newRequest
                    .url()
                    .newBuilder()
                    .addQueryParameter("csrfmiddlewaretoken", csrftoken)
                    .addQueryParameter("csrfmiddlewaretoken", csrftoken)
                    .build()
            newRequest = newRequest.newBuilder()
                    .addHeader("referer", config.host)
                    .addHeader("X-CSRFToken", csrftoken)
                    .addHeader("Cookie", cookieResult)
                    .url(url)
                    .build()
            chain.proceed(newRequest)
        }
        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder.addNetworkInterceptor(interceptor)
        //        okHttpBuilder.addNetworkInterceptor(this.stethoInterceptor);
        okHttpBuilder.setTimeoutsInSeconds(TIMEOUT_IN_SECONDS)
        val notLogged = NetworkHelper.createRetrofit(okHttpBuilder.build(), config.host)

        val tempService = notLogged.create(EmptyAuthService::class.java)
        return tempService.remindPassword(encodedEmail)
    }

    fun joinCourse(course: Long): Completable =
            stepikService.joinCourse(EnrollmentWrapper(course))

    fun getSteps(lesson: LongArray): Single<StepResponse> =
            stepikService.getSteps(lesson)

    fun getLessons(lesson: LongArray): Single<LessonStepicResponse> =
            stepikService.getLessons(lesson)

}