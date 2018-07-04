package org.stepik.android.exams.ui.activity

import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import javax.inject.Inject

class SplashActivity : BaseFragmentActivity() {

    private lateinit var disposable : Disposable

    @Inject
    lateinit var sharedPreferenceHelper: SharedPreferenceHelper

    @Inject
    lateinit var screenManager: ScreenManager

    @Inject
    @field:MainScheduler
    lateinit var mainScheduler: Scheduler

    @Inject
    @field:BackgroundScheduler
    lateinit var backgroundScheduler: Scheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.component().inject(this)
        setContentView(R.layout.activity_splash)

        val authObservable = Observable.fromCallable(sharedPreferenceHelper::authResponseDeadline)
        val onboardingObservable = Observable.fromCallable(sharedPreferenceHelper::isNotFirstTime)
        screenManager.showOnboardingScreen();
        // check if user signed in
/*       disposable =
                .delay(1L, TimeUnit.SECONDS)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe {
                    if (it.first != 0L && it.second) {
                        screenManager.startStudy()
                    } else {
                        screenManager.showOnboardingScreen()
                    }
                }*/
    }

}