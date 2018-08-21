package org.stepik.android.exams.core.presenter.contracts



interface RecommendationsView {
    fun onAdapter(cardsAdapter: QuizCardsAdapter)

    fun onLoading()
    fun onConnectivityError()
    fun onRequestError()

    fun onCourseNotSupported()
    fun onCourseCompleted()
    fun onCardLoaded()
}