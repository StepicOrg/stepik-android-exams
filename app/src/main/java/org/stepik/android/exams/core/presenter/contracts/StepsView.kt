package org.stepik.android.exams.core.presenter.contracts

import android.view.ViewGroup
import org.stepik.android.model.Step

interface StepsView {
    fun setHeader(stepViewContainer: ViewGroup, step: Step?)
}