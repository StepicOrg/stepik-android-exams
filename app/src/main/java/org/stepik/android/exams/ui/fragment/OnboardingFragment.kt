package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.App
import org.stepik.android.exams.api.auth.AuthError
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.AuthPresenter
import org.stepik.android.exams.core.presenter.contracts.AuthView
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import javax.inject.Inject

class OnboardingFragment : Fragment(), AuthView {

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

    @Inject
    lateinit var screenManager: ScreenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        App.component().inject(this)
        presenter.attachView(this)
        disposable.add(Observable.fromCallable(sharedPreferenceHelper::authResponseDeadline)
                .observeOn(mainScheduler)
                .subscribe {
                    if(it == 0L) {
                        sharedPreferenceHelper.isNotFirstTime = true
                        createMockAccount() }
                    else
                        onSuccess()
                })
    }

    private fun onComplete() {
        if (completed == 2) {
            disposable.add(sharedPreferenceHelper.isFakeUser()
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .subscribe { isFake ->
                        if (isFake) {
                            screenManager.showEmptyAuthScreen(context)
                        }
                    })
        }
    }
    private fun createMockAccount() {
        presenter.authFakeUser()
    }
    override fun onError(authError: AuthError) {
    }

    override fun onLoading() {
        completed++
        //add loading
    }

    override fun onSuccess() {
        completed++
        onComplete()
    }

    override fun onDestroy() {
        disposable.dispose()
        presenter.detachView(this)
        super.onDestroy()
    }
}