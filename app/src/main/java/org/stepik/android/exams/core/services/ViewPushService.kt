package org.stepik.android.exams.core.services

import android.app.IntentService
import android.app.Service
import android.content.Intent
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.App
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.data.model.ViewAssignment
import org.stepik.android.exams.data.model.ViewAssignmentWrapper
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.util.AppConstants
import javax.inject.Inject

class ViewPushService : IntentService("view_state_pusher") {
    @Inject
    lateinit var stepicRestService: StepicRestService
    @Inject
    @field:MainScheduler
    lateinit var mainScheduler: Scheduler
    @Inject
    @field:BackgroundScheduler
    lateinit var backgroundScheduler: Scheduler
    private val compositeDisposable = CompositeDisposable()

    init {
        App.component().inject(this)
    }

    override fun onHandleIntent(p0: Intent) {
        sendViewAssignment(p0.getParcelableExtra(AppConstants.viewPush))
    }

    private fun sendViewAssignment(viewAssignment: ViewAssignment) {
        stepicRestService.postViewed(ViewAssignmentWrapper(viewAssignment.assignment, viewAssignment.step))
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({}, {
                    onError(it) })
    }

    private fun onError(it: Throwable) {
        it.printStackTrace()
    }
}