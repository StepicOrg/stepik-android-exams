package org.stepik.android.exams.core.presenter

import io.reactivex.Scheduler
import org.stepik.android.exams.core.interactor.contacts.LessonInteractor
import org.stepik.android.exams.core.presenter.contracts.NavigateView
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import javax.inject.Inject

class NavigatePresenter
@Inject
constructor(
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler,
        private val lessonNavigatorInteractor: LessonInteractor
) : PresenterBase<NavigateView>() {

    fun navigateToLesson(step: Step?, id: String, move: Boolean) {
        if (step?.position == 1L)
            navigateToPrev(step.lesson, id, move)
        if (step?.is_last == true)
            navigateToNext(step.lesson, id, move)
    }

    private fun navigateToPrev(id: Long, topicId: String, move: Boolean) {
        lessonNavigatorInteractor.resolvePrevLesson(topicId, id, move)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ l ->
                    view?.showNavigation()
                    if (move)
                        view?.moveToLesson(l.theoryId, l.lesson)
                }, {
                    view?.hideNavigation()
                })
    }

    private fun navigateToNext(id: Long, topicId: String, move: Boolean) {
        lessonNavigatorInteractor.resolveNextLesson(topicId, id, move)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ l ->
                    view?.showNavigation()
                    if (move)
                        view?.moveToLesson(l.theoryId, l.lesson)
                }, {
                    view?.hideNavigation()
                })
    }


    override fun destroy() {
    }
}