package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import org.stepik.android.exams.api.StepikRestService
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.data.model.attempts.Attempt
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.web.AttemptRequest
import javax.inject.Inject

class StepAttemptPresenter
@Inject
constructor(
        private var stepikRestService: StepikRestService,
        @MainScheduler
        private var mainScheduler: Scheduler,
        @BackgroundScheduler
        private var backgroundScheduler: Scheduler,
        private var sharedPreference: SharedPreferenceHelper
) : PresenterBase<AttemptView>() {

    private var attempt : Attempt? = null

    override fun attachView(view: AttemptView) {
        super.attachView(view)
        if (attempt != null)
        view.trySetAttempt(attempt)
    }

    private fun getCurrentUserId() = sharedPreference.profile?.id

    fun createNewAttempt(step: Step?) {
        Observable.concat(
                stepikRestService.getExistingAttempts(step?.id ?: 0, getCurrentUserId()
                        ?: 0).toObservable(),
                stepikRestService.createNewAttempt(AttemptRequest(step?.id ?: 0)).toObservable()
        )
                .filter { it.attempts.isNotEmpty() }
                .take(1)
                .map { it.attempts.firstOrNull() }
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({
                    attempt = it
                    view?.trySetAttempt(attempt) }, { /*onError(it)*/ })
    }

    override fun destroy() {

    }
}