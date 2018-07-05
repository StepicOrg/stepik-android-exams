package org.stepik.android.exams.core.presenter.contracts

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.stepik.android.exams.api.auth.AuthError
@StateStrategyType(SingleStateStrategy::class)
interface AuthView : MvpView{
    fun onSuccess()
    fun onError(authError : AuthError)
    fun onLoading()
}