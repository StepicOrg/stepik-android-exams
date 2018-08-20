package org.stepik.android.exams.core.presenter

import io.reactivex.Scheduler
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

    fun navigateToLesson(step: Step?, id: String, lastPosition: Int, move: Boolean) {
        if (step?.position == 1L)
            navigateToPrev(step.lesson.toInt(), id, move)
        if (step?.position == lastPosition.toLong())
            navigateToNext(step.lesson.toInt(), id, move)
    }

    private fun navigateToPrev(id: Int, topicId: String, move: Boolean) {
        navigationNavigatorInteractor.resolvePrevLesson(topicId, id, move)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ l ->
                    val lesson = l.last()
                    view?.showNextButton()
                    if (move) view?.moveToLesson(lesson.theoryId, lesson.lesson)
                }, {
                    view?.hideNextButton()
                })
    }

    private fun navigateToNext(id: Int, topicId: String, move: Boolean) {
        navigationNavigatorInteractor.resolveNextLesson(topicId, id, move)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ l ->
                    val lesson = l.first()
                    view?.showPrevButton()
                    if (move) view?.moveToLesson(lesson.theoryId, lesson.lesson)
                }, {
                    view?.hidePrevButton()
                })
    }

    override fun destroy() {
    }
}