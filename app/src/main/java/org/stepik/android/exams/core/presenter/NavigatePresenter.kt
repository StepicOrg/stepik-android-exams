package org.stepik.android.exams.core.presenter

import org.stepik.android.exams.core.interactor.LessonInteractorImpl
import org.stepik.android.exams.core.presenter.contracts.NavigateView
import org.stepik.android.exams.data.model.Step
import javax.inject.Inject

class NavigatePresenter
@Inject
constructor(
        private val lessonNavigatorInteractorImpl: LessonInteractorImpl
) : PresenterBase<NavigateView>() {

    fun navigateToLesson(step: Step?, id: String) {
        if (step?.position == 1L)
            navigateToPrev(step.lesson, id)
        if (step?.is_last == true)
            navigateToNext(step.lesson, id)
    }

    private fun navigateToPrev(id: Long, topicId: String) {
        lessonNavigatorInteractorImpl.resolvePrevLesson(topicId, id)
                .subscribe({ l ->
                    view?.moveToLesson(l.theoryId, l.lesson)
                }, {})
    }

    private fun navigateToNext(id: Long, topicId: String) {
        lessonNavigatorInteractorImpl.resolveNextLesson(topicId, id)
                .subscribe({ l ->
                    view?.moveToLesson(l.theoryId, l.lesson)
                }, {})
    }


    override fun destroy() {
    }
}