package org.stepik.android.exams.core.presenter

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.core.interactor.contacts.NavigationInteractor
import org.stepik.android.exams.core.presenter.contracts.NavigateView
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.model.Step
import javax.inject.Inject

class NavigationPresenter
@Inject
constructor(
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler,
        private val navigationNavigatorInteractor: NavigationInteractor
) : PresenterBase<NavigateView>() {
    private var disposable = CompositeDisposable()
    fun navigateToLesson(step: Step?, topicId: String, lastPosition: Long, move: Boolean) {
        if (step?.position == 1L)
            navigateToPrev(topicId, step.lesson, move)
        if (step?.position == lastPosition)
            navigateToNext(topicId, step.lesson, move)
    }

    private fun navigateToPrev(topicId: String, lessonId: Long, move: Boolean) {
        disposable.add(navigationNavigatorInteractor.resolvePrevLesson(topicId, lessonId, move)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ l ->
                    val lesson = l.last()
                    view?.showNextButton()
                    if (move) view?.moveToLesson(lesson.theoryId, lesson.lesson)
                }, {
                    view?.hideNextButton()
                }))
    }

    private fun navigateToNext(topicId: String, lessonId: Long, move: Boolean) {
        disposable.add(navigationNavigatorInteractor.resolveNextLesson(topicId, lessonId, move)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ l ->
                    val lesson = l.first()
                    view?.showPrevButton()
                    if (move) view?.moveToLesson(lesson.theoryId, lesson.lesson)
                }, {
                    view?.hidePrevButton()
                }))
    }

    override fun destroy() {
        disposable.clear()
    }
}