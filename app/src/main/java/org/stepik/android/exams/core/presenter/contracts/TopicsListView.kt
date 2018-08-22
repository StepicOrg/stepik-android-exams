package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.graph.model.GraphData
import org.stepik.android.exams.graph.model.GraphLesson

interface TopicsListView {
    fun showGraphData(graphData: GraphData)
    fun setState(state: State)
    fun getParsedLessons(list : List<GraphLesson>)
    sealed class State {
        object Idle : State()
        object Loading : State()
        object Success : State()
        object NetworkError : State()
    }
}