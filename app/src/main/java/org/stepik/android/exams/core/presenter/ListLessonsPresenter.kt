package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.core.presenter.contracts.LessonsView
import org.stepik.android.exams.data.repository.LessonsRepository
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.model.GraphLesson
import javax.inject.Inject
import kotlin.properties.Delegates

class ListLessonsPresenter
@Inject
constructor(
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler,
        private val lessonsRepository: LessonsRepository
) : PresenterBase<LessonsView>() {
    private var viewState by Delegates.observable(LessonsView.State.Idle as LessonsView.State) { _, _, newState ->
        view?.setState(newState)
    }

    private val disposable = CompositeDisposable()

    fun loadAllTypedLessons(type: GraphLesson.Type) {
        val oldViewState = viewState
        viewState = if (oldViewState is LessonsView.State.Success) {
            LessonsView.State.Refreshing(oldViewState.lessons)
        } else {
            LessonsView.State.Loading
        }
        val lessonObservable = when (type) {
            GraphLesson.Type.THEORY -> loadTheoryLessons()
            GraphLesson.Type.PRACTICE -> loadPracticeLessons()
        }
        disposable.add(lessonObservable
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe({ lessons ->
                    viewState = LessonsView.State.Success(lessons)
                }, {
                    viewState = LessonsView.State.NetworkError
                }))
    }

    private fun loadTheoryLessons() =
            lessonsRepository.loadAllTheoryLessons()

    private fun loadPracticeLessons() =
            lessonsRepository.loadAllPracticeLessons()

    override fun attachView(view: LessonsView) {
        super.attachView(view)
        view.setState(viewState)
    }

    override fun destroy() {
        disposable.clear()
    }
}