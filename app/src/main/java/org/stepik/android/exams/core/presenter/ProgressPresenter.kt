package org.stepik.android.exams.core.presenter

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.zipWith
import io.reactivex.subjects.BehaviorSubject
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.contracts.ProgressView
import org.stepik.android.exams.data.db.entity.ProgressEntity
import org.stepik.android.exams.data.model.ViewAssignment
import org.stepik.android.exams.data.repository.AssignmentRepository
import org.stepik.android.exams.data.repository.ProgressRepository
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.LessonProgressUpdatesBus
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.util.addDisposable
import org.stepik.android.model.Progress
import org.stepik.android.model.Step
import org.stepik.android.model.Unit
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
        private val progressRepository: ProgressRepository,
        private val screenManager: ScreenManager,
        private val assignmentRepository: AssignmentRepository,

        @LessonProgressUpdatesBus
        private val lessonProgressUpdatesBus : BehaviorSubject<Long>
) : PresenterBase<ProgressView>() {

    private val disposable = CompositeDisposable()

    fun isStepPassed(step: Step) {
        val progress = step.progress ?: ""
        disposable.addDisposable(service.getProgresses(arrayOf(progress))
                .delay(400, TimeUnit.MILLISECONDS)
                .map { it.progresses.first().isPassed }
                .flatMap { isPassed ->
                    updateProgress(step, isPassed)
                            .andThen(Single.just(isPassed))
                }
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribeBy({}) { isPassed ->
                    if (isPassed) {
                        lessonProgressUpdatesBus.onNext(step.lesson)
                    }
                    view?.markedAsView(step.copy(isCustomPassed = isPassed))
                })
    }

    fun isAllStepsPassed(steps: List<Step>) {
        val progresses = steps.mapNotNull(Step::progress).toTypedArray()
        disposable.addDisposable(service.getProgresses(progresses)
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
                }) {
                    it.sortBy { step -> step.position }
                    view?.markedAsView(it)
                })
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
            disposable.addDisposable(getStepProgress(step.id)
                    .onErrorReturnItem (false)
                    .flatMapCompletable { isPassed ->
                        if (isPassed) {
                            Completable.complete()
                        } else {
                            markStepAsPassed(course, step)
                        }
                    }
                    .observeOn(mainScheduler)
                    .subscribeOn(backgroundScheduler)
                    .subscribe({
                        step.isCustomPassed = true
                        view?.markedAsView(step)
                    }, {}))
        }
    }

    private fun markStepAsPassed(course: Long, step: Step): Completable =
            service.getUnits(course, step.lesson)
                    .map { it.units?.firstOrNull() }
                    .flatMap { assignmentRepository.getStepAssignment(step.id, it.id).zipWith(Single.just(it)) }
                    .flatMap { (assignmentLocal, unit) ->
                        if (assignmentLocal == 0L) {
                            loadAssignmentsFromApi(unit, step)
                        } else {
                            Single.just(assignmentLocal)
                        }
                    }.flatMapCompletable { assignment ->
                        val stepId = step.id
                        screenManager.pushToViewedQueue((ViewAssignment(assignment, stepId)))
                        updateProgress(step, true)
                    }.doOnComplete {
                        lessonProgressUpdatesBus.onNext(step.lesson)
                    }

    private fun loadAssignmentsFromApi(unit: Unit, step: Step): Single<Long> =
            assignmentRepository.getAssignmentApi(unit)
                    .flatMap { list ->
                        assignmentRepository.insertAssignments(list).andThen(Single.just(list.first { it.step == step.id }.id))
                    }

    private fun updateProgress(step: Step, isPassed: Boolean) =
            Completable.fromCallable { progressRepository.insertProgresses(listOf(ProgressEntity(step.id, step.lesson, isPassed, step.progress!!))) }

    private fun getStepProgress(stepId: Long): Single<Boolean> =
            progressRepository.getStepProgressLocal(stepId)

    override fun destroy() {
        disposable.clear()
    }
}