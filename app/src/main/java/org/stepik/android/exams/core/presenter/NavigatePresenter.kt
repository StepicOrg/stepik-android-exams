package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import org.stepik.android.exams.core.presenter.contracts.NavigateView
import org.stepik.android.exams.data.db.dao.NavigationDao
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import javax.inject.Inject

class NavigatePresenter
@Inject
constructor(
        val navigationDao: NavigationDao,
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler
) : PresenterBase<NavigateView>() {

    fun navigateToLesson(step: Step?) {
        if (step?.position == 1L)
            navigateToPrev(step.lesson)
        if (step?.is_last == true)
            navigateToNext(step.lesson)
    }

    private fun navigateToPrev(id: Long) {
        Observable.fromCallable { navigationDao.findPrevIdByLessonId(id)?.lesson }
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe { lesson ->
                    view?.moveToLesson(lesson)
                }
    }

    private fun navigateToNext(id: Long) {
        Observable.fromCallable { navigationDao.findNextIdByLessonId(id)?.lesson }
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe { lesson ->
                    view?.moveToLesson(lesson)
                }
    }


    override fun destroy() {
    }
}