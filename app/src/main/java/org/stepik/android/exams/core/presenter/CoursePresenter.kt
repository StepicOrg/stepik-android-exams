package org.stepik.android.exams.core.presenter

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.core.presenter.contracts.CourseView
import org.stepik.android.exams.data.repository.CoursesRepository
import org.stepik.android.exams.data.repository.TopicsRepository
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import javax.inject.Inject

class CoursePresenter
@Inject
constructor(
        private val coursesRepository: CoursesRepository,
        private val topicsRepository: TopicsRepository,
        @MainScheduler
        private val mainScheduler: Scheduler,
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler
) : PresenterBase<CourseView>() {
    private val disposable = CompositeDisposable()

    fun continueEducation() {
        disposable.add(
                coursesRepository.getLastStep()
                        .map { it.step }
                        .flatMap { topicsRepository.getTopicByStep(it) }
                        .subscribeOn(backgroundScheduler)
                        .observeOn(mainScheduler)
                        .subscribe({
                            view?.initContinueEducation(it)
                        }, {
                            it
                        }))

    }

    override fun destroy() {
        disposable.clear()
    }
}