package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.core.presenter.contracts.LessonsView
import org.stepik.android.exams.data.model.LessonTheoryWrapper
import org.stepik.android.exams.data.repository.StepsRepository
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.model.GraphLesson
import org.stepik.android.model.Lesson
import javax.inject.Inject
import kotlin.properties.Delegates

class LessonsPresenter
@Inject
constructor(
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler,
        private val stepsRepository: StepsRepository
) : PresenterBase<LessonsView>() {
    private var viewState by Delegates.observable(LessonsView.State.Idle as LessonsView.State) { _, _, newState ->
        view?.setState(newState)
    }

    private val disposable = CompositeDisposable()

    fun tryLoadLessons(topicId: String) {
        val oldViewState = viewState
        viewState = if (oldViewState is LessonsView.State.Success) {
            LessonsView.State.Refreshing(oldViewState.lessons)
        } else {
            LessonsView.State.Loading
        }
        disposable.add(stepsRepository.tryLoadLessons(theoryId = topicId)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ theoryLessons ->
                    viewState = LessonsView.State.Success(theoryLessons.map(LessonTheoryWrapper::lesson))
                }, {
                    viewState = LessonsView.State.NetworkError
                }))
    }
    private fun loadPracticeLesson(topicId: String) =
        stepsRepository.getCoursesId(topicId, GraphLesson.Type.PRACTICE)

    override fun attachView(view: LessonsView) {
        super.attachView(view)
        view.setState(viewState)
    }

    override fun destroy() {
        disposable.clear()
    }
}