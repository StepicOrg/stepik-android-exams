package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.api.auth.AuthError

interface AuthView {
    fun onSuccess()
    fun onError(authError: AuthError)
    fun onLoading()
}