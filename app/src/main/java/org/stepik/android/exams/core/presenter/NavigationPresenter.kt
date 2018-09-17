package org.stepik.android.exams.core.presenter

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.core.interactor.contacts.NavigationInteractor
import org.stepik.android.exams.core.presenter.contracts.NavigateView
import org.stepik.android.exams.data.db.dao.TopicDao
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.model.GraphLesson
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
    private var disposable = CompositeDisposable()
    fun navigateToLesson(step: Step?, topicId: String, lastPosition: Long, move: Boolean) {
        if (step?.position == 1L)
            navigateToPrev(topicId, step.lesson, move)
        if (step?.position == lastPosition)
            navigateToNext(topicId, step.lesson, move)
    }

    private fun getLessonId(topicId: String) =
            topicDao.getTopicInfoByType(topicId, GraphLesson.Type.THEORY)

    private fun navigateToPrev(topicId: String, lessonId: Long, move: Boolean) {
        disposable.add(getLessonId(topicId)
                .flatMapObservable { navigationNavigatorInteractor.resolvePrevLesson(topicId, lessonId, move, it.toLongArray()) }
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ wrappers ->
                    val lessonWrapper = wrappers.last()
                    view?.showNextButton()
                    if (move) view?.moveToLesson(lessonWrapper.topicId, lessonWrapper)
                }, {
                    view?.hideNextButton()
                }))
    }

    private fun navigateToNext(topicId: String, lessonId: Long, move: Boolean) {
        disposable.add(getLessonId(topicId)
                .flatMapObservable { navigationNavigatorInteractor.resolveNextLesson(topicId, lessonId, move, it.toLongArray()) }
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ wrappers ->
                    val lessonWrapper = wrappers.first()
                    view?.showPrevButton()
                    if (move) view?.moveToLesson(lessonWrapper.topicId, lessonWrapper)
                }, {
                    view?.hidePrevButton()
                }))
    }

    override fun destroy() {
        disposable.clear()
    }
}