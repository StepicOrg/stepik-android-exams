package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.core.presenter.contracts.LessonsView
import org.stepik.android.exams.data.repository.StepsRepository
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import javax.inject.Inject
import kotlin.properties.Delegates

class ListLessonsPresenter
@Inject
constructor(
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler,
        private val stepsRepository: StepsRepository
) : PresenterBase<LessonsView>() {
    companion object {
        const val THEORY_TYPE = "theory"
        const val PRACTICE_TYPE = "practice"
    }
    private var viewState by Delegates.observable(LessonsView.State.Idle as LessonsView.State) { _, _, newState ->
        view?.setState(newState)
    }

    private val disposable = CompositeDisposable()

    fun loadAllTypedLessons(type: String) {
        val oldViewState = viewState
        viewState = if (oldViewState is LessonsView.State.Success) {
            LessonsView.State.Refreshing(oldViewState.lessons)
        } else {
            LessonsView.State.Loading
        }
        val lessonObservable = when (type) {
            THEORY_TYPE -> loadTheoryLessons()
            PRACTICE_TYPE -> loadPracticeLessons()
            else -> Observable.empty()
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
            stepsRepository.loadAllTheoryLessons()

    private fun loadPracticeLessons() =
            stepsRepository.loadAllPracticeLessons()

    override fun attachView(view: LessonsView) {
        super.attachView(view)
        view.setState(viewState)
    }

    override fun destroy() {
        disposable.clear()
    }
}