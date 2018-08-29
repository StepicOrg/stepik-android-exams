package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.core.presenter.contracts.LessonsView
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.data.repository.StepsRepository
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.model.GraphLesson
import org.stepik.android.model.Lesson
import javax.inject.Inject

class LessonsPresenter
@Inject
constructor(
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler,
        private val stepsRepository: StepsRepository
) : PresenterBase<LessonsView>() {
    private var viewState: LessonsView.State = LessonsView.State.Idle
        set(value) {
            field = value
            view?.setState(value)
        }
    private var topicId: String = ""
    private var disposable = CompositeDisposable()
    private var lessonsList: List<LessonWrapper>? = null

    init {
        viewState = LessonsView.State.FirstLoading
    }

    fun tryLoadLessons(topicId: String) {
        this.topicId = topicId
        viewState = LessonsView.State.Loading
        disposable.add(stepsRepository.tryLoadLessons(theoryId = topicId)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ l ->
                    val list = l.map { it.lesson }
                    lessonsList = list
                    onComplete(list)
                }, {
                    onError()
                }))
    }
    private fun loadPracticeLesson(topicId: String) =
        stepsRepository.getCoursesId(topicId, GraphLesson.Type.PRACTICE)

    private fun onComplete(list: List<LessonWrapper>) {
        view?.showLessons(list)
        viewState = LessonsView.State.Success
    }

    override fun attachView(view: LessonsView) {
        super.attachView(view)
        view.setState(viewState)
        lessonsList?.let(view::showLessons)
    }

    private fun onError() {
        viewState = LessonsView.State.NetworkError
    }

    override fun destroy() {
        disposable.clear()
    }
}