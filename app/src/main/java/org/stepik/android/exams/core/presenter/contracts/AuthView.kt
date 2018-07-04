package org.stepik.android.exams.core.presenter.contracts

import com.arellomobile.mvp.MvpView
import org.stepik.android.exams.api.auth.AuthError

interface AuthView : MvpView {
    fun onSuccess()
    fun onError(authError : AuthError) //TODO: add error handling
    fun onLoading()
}