package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.graph.model.GraphData

interface ListView {
    fun onSuccess()
    fun loadData(graphData: GraphData)
}