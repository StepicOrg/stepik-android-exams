package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.graph.model.GraphData
import org.stepik.android.exams.graph.model.Topic

interface TopicsListView {
    fun showGraphData(graphData: GraphData)
    fun setState(state: State)
    sealed class State {
        object Idle : State()
        object Loading : State()
        class Success(val topics: List<Topic>) : State()
        class Refreshing(val topics: List<Topic>) : State()
        object NetworkError : State()
    }
}