package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.api.Errors
import org.stepik.android.exams.graph.model.GraphData

interface TopicsListView {
    fun showGraphData(graphData: GraphData)
    fun onError(error : Errors)
}