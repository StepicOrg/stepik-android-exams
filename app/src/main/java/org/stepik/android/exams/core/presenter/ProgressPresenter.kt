package org.stepik.android.exams.core.presenter

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.core.presenter.contracts.ProgressView
import org.stepik.android.exams.data.db.dao.StepDao
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
        private val mainScheduler: Scheduler,
        private val stepDao: StepDao
) : PresenterBase<ProgressView>() {

    private val disposable = CompositeDisposable()

    fun isStepPassed(step: Step?) {
        fetchProgressFromDb(step?.id!!)
                .subscribe { stepInfo ->
                    if (!stepInfo.isPassed) {
                        val progress = step.progress ?: ""
                        disposable.add(service.getProgresses(arrayOf(progress))
                                .subscribeOn(backgroundScheduler)
                                .observeOn(mainScheduler)
                                .delay(400, TimeUnit.MILLISECONDS)
                                .doOnSuccess {
                                    val isPassed = it.progresses.first().isPassed
                                    step.is_custom_passed = isPassed
                                    view?.markedAsView(step)
                                    updateProgress(step.id, isPassed).subscribe()
                                }
                                .subscribe())
                    }
                }
    }

    fun isAllStepsPassed(steps: List<Step>) {
        val progress = Array(steps.size) { "it = $it" }
        steps.forEachIndexed { index, step ->
            progress[index] = step.progress ?: ""
        }
        var index  = 0
        disposable.add(service.getProgresses(progress)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .doOnError {
                    it.printStackTrace()
                }
                .doOnSuccess { p ->
                    Observable.fromIterable(p.progresses)
                            .map { progress ->
                                fetchProgressFromDb(steps[index].id)
                                        .subscribe({ step ->
                                            val progressLocal: Boolean
                                            if (step.isPassed) {
                                                progressLocal = step.isPassed
                                                steps[index].is_custom_passed = progressLocal
                                            } else {
                                                progressLocal = progress.isPassed
                                                steps[index].is_custom_passed = progressLocal
                                            }
                                            updateProgress(steps[index].id, progressLocal).subscribe()
                                            index++
                                        }, {})
                            }
                            .subscribe()
                    view?.markedAsView(steps)
                }
                .subscribe())
    }

    fun stepPassedLocal(step: Step?) {
        if (step?.block?.name == "text") {
            step.is_custom_passed = true
            updateProgress(step.id, true)
                    .observeOn(mainScheduler)
                    .subscribe {
                        view?.markedAsView(step)
                    }
        }
    }

    private fun fetchProgressFromDb(id: Long) =
            Maybe.fromCallable { stepDao.findStepById(id) }
                    .subscribeOn(backgroundScheduler)

    private fun updateProgress(id: Long, progress: Boolean) =
            Maybe.fromCallable { stepDao.updateStepProgress(id, progress) }
                    .subscribeOn(backgroundScheduler)

    override fun destroy() {
        disposable.clear()
    }
}