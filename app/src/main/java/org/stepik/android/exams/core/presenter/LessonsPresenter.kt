package org.stepik.android.exams.core.presenter

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.core.presenter.contracts.LessonsView
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.data.repository.StepsRepository
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
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
    private var id: String = ""
    private var disposable = CompositeDisposable()
    private var lessonsList: List<LessonWrapper>? = null

    init {
        viewState = LessonsView.State.FirstLoading
    }

    fun tryLoadLessons(id: String) {
        this.id = id
        viewState = LessonsView.State.Loading
        disposable.add(stepsRepository.tryLoadLessons(theoryId = id)
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

    private fun onComplete(list: List<LessonWrapper>) {
        view?.showLessons(list)
        viewState = LessonsView.State.Success
    }

    override fun attachView(view: LessonsView) {
        super.attachView(view)
        view.setState(viewState)
        lessonsList?.let {
            if (it.isNotEmpty())
                view.showLessons(it)
        }
    }

    private fun onError() {
        viewState = LessonsView.State.NetworkError
    }

    override fun destroy() {
        disposable.clear()
    }
}