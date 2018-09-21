package org.stepik.android.exams.core.presenter

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.contracts.ProgressView
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.model.UnitMetaResponse
import org.stepik.android.exams.data.model.ViewAssignment
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.model.Progress
import org.stepik.android.model.Step
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProgressPresenter
@Inject
constructor(
        private val service: StepicRestService,
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler,
        private val stepDao: StepDao,
        private val stepicRestService: StepicRestService,
        private val screenManager: ScreenManager
) : PresenterBase<ProgressView>() {

    private val disposable = CompositeDisposable()

    fun isStepPassed(step: Step) {
        val progress = step.progress ?: ""
        service.getProgresses(arrayOf(progress))
                .delay(400, TimeUnit.MILLISECONDS)
                .map { it.progresses.first().isPassed }
                .doOnSuccess {
                    stepDao.updateStepProgress(step.id, it)
                }
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribeBy({}) {
                    view?.markedAsView(step.copy(isCustomPassed = it))
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
            fetchProgressFromDb(step.id).map { info ->
                info.isPassed || progress.isPassed
            }.doOnSuccess {
                updateProgress(step.id, it)
            }.map {
                step.copy(isCustomPassed = it)
            }.toObservable()

    fun stepPassedLocal(step: Step?, course: Long) {
        if (step?.block?.name == "text") {
            getStepProgress(step.id)
                    .onErrorResumeNext { Single.just(false) }
                    .flatMapCompletable { isPassed ->
                        step.isCustomPassed = true
                        if (isPassed) {
                            Completable.complete()
                        } else {
                            stepicRestService.getUnits(course, step.lesson)
                                    .flatMapCompletable { response ->
                                        val unit = response.units?.firstOrNull()
                                        val stepId = step.id
                                        unit?.assignments?.get((step.position.toInt() - 1))?.let { assignmentId ->
                                            screenManager.pushToViewedQueue((ViewAssignment(assignmentId, stepId)))
                                        }
                                        updateProgress(step.id, true)
                                    }
                        }
                    }
                    .observeOn(mainScheduler)
                    .subscribeOn(backgroundScheduler)
                    .subscribe({
                        view?.markedAsView(step)
                    }, {})
        }
    }

    private fun fetchProgressFromDb(id: Long) =
            Single.fromCallable { stepDao.findStepById(id) }

    private fun updateProgress(id: Long, progress: Boolean) =
            Completable.fromCallable { stepDao.updateStepProgress(id, progress) }

    private fun getStepProgress(stepId: Long) =
            Single.fromCallable { stepDao.getStepProgress(stepId) }

    override fun destroy() {
        disposable.clear()
    }
}