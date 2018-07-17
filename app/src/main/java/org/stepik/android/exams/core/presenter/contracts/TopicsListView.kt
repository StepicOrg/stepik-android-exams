package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.api.Errors
import org.stepik.android.exams.graph.model.GraphData

interface TopicsListView {
    fun showGraphData(graphData: GraphData)
    fun onError(error: Errors)
    fun hideRefreshView()
    fun showRefreshView()
    fun showErrorMessage(messageResId: Int)
    fun hideErrorMessage()
    fun setState(state: State)

    sealed class State {
        object Idle : State()
        object Loading : State()
        object Success : State()
        object NetworkError : State()
    }
}