package org.stepik.android.exams.core.presenter

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.core.interactor.contacts.NavigationInteractor
import org.stepik.android.exams.core.presenter.contracts.NavigateView
import org.stepik.android.exams.data.db.dao.TopicDao
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
        private val navigationNavigatorInteractor: NavigationInteractor,
        private val topicDao: TopicDao
) : PresenterBase<NavigateView>() {
    companion object {
        const val type = "theory"
    }

    private var disposable = CompositeDisposable()
    fun navigateToLesson(step: Step?, id: String, lastPosition: Int, move: Boolean) {
        if (step?.position == 1L)
            navigateToPrev(step.lesson, id, move)
        if (step?.position == lastPosition.toLong())
            navigateToNext(step.lesson, id, move)
    }

    private fun getLessonId(topicId: String) =
            topicDao.getTopicInfoByType(topicId, type)

    private fun navigateToPrev(id: Long, topicId: String, move: Boolean) {
        disposable.add(getLessonId(topicId).flatMapObservable { navigationNavigatorInteractor.resolvePrevLesson(topicId, id, move, it.toLongArray()) }
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

    private fun navigateToNext(id: Long, topicId: String, move: Boolean) {
        disposable.add(getLessonId(topicId).flatMapObservable { navigationNavigatorInteractor.resolveNextLesson(topicId, id, move, it.toLongArray()) }
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