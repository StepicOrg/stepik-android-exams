package org.stepik.android.exams.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    private lateinit var disposable: Disposable

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
        val onboardingObservable = Observable.fromCallable(sharedPreferenceHelper::isNotFirstTime)
        val authObservable = Observable.fromCallable(sharedPreferenceHelper::authResponseDeadline)
        disposable = Completable.complete().andThen(io.reactivex.Observable.zip<kotlin.Long, kotlin.Boolean, kotlin.Pair<kotlin.Long, kotlin.Boolean>>(authObservable, onboardingObservable, BiFunction { t1, t2 -> Pair(t1, t2) }))
                .delay(1L, java.util.concurrent.TimeUnit.SECONDS)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe {
                    if (it.first != 0L && it.second) {
                        screenManager.showMainMenu()
                    } else {
                        screenManager.showOnboardingScreen()
                    }
                }

    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}