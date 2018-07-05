package org.stepik.android.exams.api
import org.stepik.android.exams.util.Util
import org.stepik.android.exams.data.model.AccountCredentials
import javax.inject.Inject
import javax.inject.Named
import org.stepik.android.exams.configuration.Config
import org.stepik.android.exams.util.AppConstants
import org.stepik.android.exams.api.auth.CookieHelper

class Api @Inject
constructor(
        private val config: Config,
        @Named(AppConstants.userAgentName)
        private val userAgent: String,
        private val cookieHelper: CookieHelper) {
    companion object {
        private const val FAKE_MAIL_PATTERN = "adaptive_%s_android_%d%s@stepik.org"
        private const val TIMEOUT_IN_SECONDS = 60L
    }
    fun createFakeAccount(): AccountCredentials {
        val email = String.format(FAKE_MAIL_PATTERN, config.courseId, System.currentTimeMillis(), Util.randomString(5))
        val password = Util.randomString(16)
        val firstName = Util.randomString(10)
        val lastName = Util.randomString(10)
        return AccountCredentials(email, password, firstName, lastName)
    }

}