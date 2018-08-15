package org.stepik.android.exams.core.presenter

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.core.presenter.contracts.ProgressView
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.model.Progress
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
                                .delay(400, TimeUnit.MILLISECONDS)
                                .subscribeOn(backgroundScheduler)
                                .observeOn(mainScheduler)
                                .doOnSuccess {
                                    val isPassed = it.progresses.first().isPassed
                                    step.is_custom_passed = isPassed
                                    updateProgress(step.id, true)
                                            .observeOn(mainScheduler)
                                            .subscribe { _ ->
                                                view?.markedAsView(step)
                                            }
                                }
                                .subscribe())
                    }
                }
    }

    fun isAllStepsPassed(steps: List<Step>) {
        val progresses = steps.mapNotNull(Step::progress).toTypedArray()
        service.getProgresses(progresses)
                .flatMapObservable { it.progresses.toObservable() }
                .flatMap { progress ->
                    val step = steps.find { it.progress == progress.id }
                            ?: return@flatMap Observable.empty<Step>()
                    resolveStepProgress(step, progress)
                }
                .toList()
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribeBy({
                    // handle error
                }) {
                    it.sortBy { step -> step.position }
                    view?.markedAsView(it)
                }
    }

    private fun resolveStepProgress(step: Step, progress: Progress): Observable<Step> =
            fetchProgressFromDb(step.id).flatMap { info ->
                val isPassed = info.isPassed || progress.isPassed
                updateProgress(step.id, isPassed)
                return@flatMap Single.just(isPassed)
            }.flatMapObservable {
                Observable.just(step.copy(is_custom_passed = it))
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
                    .observeOn(mainScheduler)
                    .toSingle()

    private fun updateProgress(id: Long, progress: Boolean) =
            Maybe.fromCallable { stepDao.updateStepProgress(id, progress) }
                    .subscribeOn(backgroundScheduler)

    override fun destroy() {
        disposable.clear()
    }
}