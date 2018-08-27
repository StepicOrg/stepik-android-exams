package org.stepik.android.exams.adaptive.core.contracts

import org.stepik.android.exams.adaptive.ui.adapter.QuizCardsAdapter


interface RecommendationsView {
    fun onAdapter(cardsAdapter: QuizCardsAdapter)

    fun onLoading()
    fun onConnectivityError()
    fun onRequestError()

    fun onCourseNotSupported()
    fun onCourseCompleted()
    fun onCardLoaded()

    fun setState(state: State)
    sealed class State {
        object InitPresenter : State()
        object Loading : State()
        object Success : State()
        object RequestError : State()
        object NetworkError : State()
        object CourseCompleted : State()
    }
}