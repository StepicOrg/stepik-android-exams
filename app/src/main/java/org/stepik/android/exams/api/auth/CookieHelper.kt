package org.stepik.android.exams.api.auth

import android.os.Looper
import android.webkit.CookieManager
import io.reactivex.Completable
import io.reactivex.Scheduler
import okhttp3.Request
import org.stepik.android.exams.configuration.Config
import org.stepik.android.exams.di.AppSingleton
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.util.AppConstants
import org.stepik.android.exams.util.Util
import java.net.HttpCookie
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import javax.inject.Inject

@AppSingleton
class CookieHelper
@Inject constructor(
        private val emptyAuthService: EmptyAuthService,
        private val cookieManager: CookieManager,
        private val config: Config,
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler
) {
    companion object {
        private fun tryGetCsrfFromOnePair(keyValueCookie: String): String? =
                HttpCookie.parse(keyValueCookie).find { it.name == AppConstants.csrfTokenCookieName }?.value

        private fun getCsrfTokenFromCookies(cookies: String): String {
            cookies.split(";").forEach {
                val csrf = tryGetCsrfFromOnePair(it)
                if (csrf != null) return csrf
            }
            return ""
        }
    }

    fun removeCookiesCompat() {
        if (Util.isLowAndroidVersion()) {
            @Suppress("DEPRECATION")
            cookieManager.removeAllCookie()
        } else {
            Completable.fromRunnable {
                Looper.prepare()
                cookieManager.removeAllCookies(null)
                Looper.loop()
            }.subscribeOn(backgroundScheduler).subscribe()
        }
    }

    fun addCsrfTokenToRequest(request: Request): Request {
        val cookies = cookieManager.getCookie(config.host)
        return if (cookies == null) {
            request
        } else {
            val csrftoken = getCsrfTokenFromCookies(cookies)
            request.newBuilder()
                    .addHeader(AppConstants.refererHeaderName, config.host)
                    .addHeader(AppConstants.csrfTokenHeaderName, csrftoken)
                    .addHeader(AppConstants.cookieHeaderName, cookies)
                    .build()
        }
    }

    fun getCookiesForBaseUrl(): List<HttpCookie>? {
        val lang = Locale.getDefault().language
        val response = emptyAuthService.getStepicForFun(lang).execute()

        val cookieManager = java.net.CookieManager()
        val myUri: URI
        try {
            myUri = URI(config.host)
        } catch (e: URISyntaxException) {
            return null
        }

        cookieManager.put(myUri, response.headers().toMultimap())
        return cookieManager.cookieStore.get(myUri)
    }

    fun updateCookieForBaseUrl() {
        val lang = Locale.getDefault().language
        val response = emptyAuthService.getStepicForFun(lang).execute()

        val setCookieHeaders = response.headers().values(AppConstants.setCookieHeaderName)
        for (value in setCookieHeaders) {
            if (value != null) {
                cookieManager.setCookie(config.host, value) //set-cookie is not empty
            }
        }
    }
}