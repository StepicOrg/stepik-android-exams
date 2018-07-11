package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.graph.model.GraphData

interface StudyView {
    fun onSuccess()
    fun loadData(graphData: GraphData)
}