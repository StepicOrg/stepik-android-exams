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
}