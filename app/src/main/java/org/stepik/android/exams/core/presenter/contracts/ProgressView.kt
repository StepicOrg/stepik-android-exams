package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.data.model.Step

interface ProgressView {
    fun markedAsView(steps: List<Step>)
    fun markedAsView(step: Step)
}