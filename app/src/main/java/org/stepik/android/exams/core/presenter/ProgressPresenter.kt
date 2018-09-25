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
import org.stepik.android.exams.data.db.dao.ProgressDao
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.db.entity.ProgressEntity
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
        private val progressDao: ProgressDao,
        private val stepDao: StepDao,
        private val screenManager: ScreenManager
) : PresenterBase<ProgressView>() {

    private val disposable = CompositeDisposable()

    fun isStepPassed(step: Step) {
        val progress = step.progress ?: ""
        service.getProgresses(arrayOf(progress))
                .delay(400, TimeUnit.MILLISECONDS)
                .map { it.progresses.first().isPassed }
                .doOnSuccess {
                    updateProgress(step, it)
                }
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribeBy({}) {isPassed ->
                    view?.markedAsView(step.copy(isCustomPassed = isPassed))
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
            getStepProgress(step.id).map { isPassed ->
                isPassed || progress.isPassed
            }.doOnSuccess {
                updateProgress(step, it)
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
                            service.getUnits(course, step.lesson)
                                    .flatMap {response -> service.getAssignments(response.units?.firstOrNull()?.assignments!!).map { it.assignments }}
                                    .flatMapCompletable { assignments ->
                                        val stepId = step.id
                                        val assignment = assignments.first { it.step == stepId }
                                        screenManager.pushToViewedQueue((ViewAssignment(assignment.id, stepId)))
                                        updateProgress(step, true)
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

    private fun updateProgress(step: Step, isPassed: Boolean) =
            Completable.fromCallable { progressDao.insertStepProgress(ProgressEntity(step.id, isPassed, step.progress!!)) }

    private fun getStepProgress(stepId: Long) =
            Single.fromCallable { stepDao.getStepProgress(stepId) }

    override fun destroy() {
        disposable.clear()
    }
}