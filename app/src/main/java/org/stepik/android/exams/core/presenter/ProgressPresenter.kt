package org.stepik.android.exams.core.presenter

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.core.presenter.contracts.ProgressView
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProgressPresenter
@Inject
constructor(
        val service: StepicRestService,
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler
) : PresenterBase<ProgressView>() {

    private val disposable = CompositeDisposable()

    fun passLocalStep(step: Step?) {
        step?.is_custom_passed = true
        view?.markedAsView(step as Step)
    }

    fun isStepPassed(step: Step?) {
        val progress = step?.progress ?: ""
        disposable.add(service.getProgresses(arrayOf(progress))
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .delay(300, TimeUnit.MILLISECONDS)
                .doOnSuccess {
                    step?.is_custom_passed = it.progresses.first().isPassed
                    view?.markedAsView(step as Step)
                }
                .subscribe())
    }

    fun isAllStepsPassed(steps: MutableList<Step>) {
        val progress = Array(steps.size) { "it = $it" }
        steps.forEachIndexed { index, step ->
            progress[index] = step.progress ?: ""
        }
        disposable.add(service.getProgresses(progress)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .doOnError {
                    it.printStackTrace()
                }
                .doOnSuccess {
                    it.progresses.forEachIndexed { index, progress ->
                        steps[index].is_custom_passed = progress.isPassed
                    }
                    view?.markedAsView(steps)
                }
                .subscribe())
    }

    override fun destroy() {
        disposable.clear()
    }
}