package org.stepik.android.exams.api

import org.stepik.android.exams.util.Util
import org.stepik.android.exams.data.model.AccountCredentials
import javax.inject.Inject

class Api @Inject
constructor() {
    companion object {
        private const val FAKE_MAIL_PATTERN = "exams_%s_android_%d%s@stepik.org"
        private const val TIMEOUT_IN_SECONDS = 60L
    }
    fun createFakeAccount(): AccountCredentials {
        val email = String.format(FAKE_MAIL_PATTERN, System.currentTimeMillis(), Util.randomString(5))
        val password = Util.randomString(16)
        val firstName = Util.randomString(10)
        val lastName = Util.randomString(10)
        return AccountCredentials(email, password, firstName, lastName)
    }

}