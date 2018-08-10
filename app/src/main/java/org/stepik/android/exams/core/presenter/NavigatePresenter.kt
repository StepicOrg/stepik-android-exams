package org.stepik.android.exams.core.presenter

import io.reactivex.Scheduler
import org.stepik.android.exams.core.interactor.LessonInteractor
import org.stepik.android.exams.core.presenter.contracts.NavigateView
import org.stepik.android.exams.data.db.dao.NavigationDao
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import javax.inject.Inject

class NavigatePresenter
@Inject
constructor(
        private val navigationDao: NavigationDao,
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler,
        private val lessonNavigatorInteractor: LessonInteractor
) : PresenterBase<NavigateView>() {

    fun navigateToLesson(step: Step?) {
        if (step?.position == 1L)
            navigateToPrev(step.lesson)
        if (step?.is_last == true)
            navigateToNext(step.lesson)
    }

    private fun navigateToPrev(id: Long) {
        navigationDao.findPrevByLessonId(id)
                .subscribeOn(backgroundScheduler)
                .subscribe({ info ->
                    view?.moveToLesson(info?.lesson)
                }, {
                    lessonNavigatorInteractor.resolvePrevLesson(id)
                })
    }

    private fun navigateToNext(id: Long) {
        navigationDao.findNextByLessonId(id)
                .subscribeOn(backgroundScheduler)
                .subscribe({ info ->
                    view?.moveToLesson(info?.lesson)
                }, {
                    lessonNavigatorInteractor.resolveNextLesson(id)
                })
    }


    override fun destroy() {
    }
}