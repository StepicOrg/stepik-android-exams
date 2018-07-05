package org.stepik.android.exams.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.App
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.api.auth.AuthError
import org.stepik.android.exams.api.auth.AuthRepository
import org.stepik.android.exams.core.presenter.AuthPresenter
import org.stepik.android.exams.core.presenter.contracts.AuthView
import org.stepik.android.exams.data.preference.ProfilePreferences
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.ui.activity.RegisterActivity
import javax.inject.Inject

class OnboardingFragment : MvpAppCompatFragment(), AuthView {
    override fun onError(authError: AuthError) {
        TODO("not implemented")
    }

    override fun onLoading() {
        startActivity(Intent(context, RegisterActivity::class.java))
    }

    override fun onSuccess() {
        startActivity(Intent(context, RegisterActivity::class.java))
    }

    private var completed = 0

    private val disposable = CompositeDisposable()

    @Inject
    @field:MainScheduler
    lateinit var mainScheduler: Scheduler

    @Inject
    @field:BackgroundScheduler
    lateinit var backgroundScheduler: Scheduler

    @Inject
    lateinit var presenter: AuthPresenter

    @Inject
    lateinit var sharedPreferenceHelper: SharedPreferenceHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        App.component().inject(this)
        createMockAccount();
        // register fake user
/*        disposable.add(Observable.fromCallable(sharedPreferenceHelper::authResponseDeadline)
                .observeOn(mainScheduler)
                .subscribe {
                    if(it == 0L)
                        createMockAccount()
                    else
                        onSuccess()
                })*/
    }

/*    private fun onComplete() {
        if (completed == 2) {
            disposable addDisposable sharedPreferenceHelper.isFakeUser()
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .subscribe { isFake ->
                        // -> go go home
                        if (isFake) {
                            screenManager.showEmptyAuthScreen(context)
                        }
                    }
        }
    }*/
    private fun createMockAccount() {
        presenter.authFakeUser()
    }
}