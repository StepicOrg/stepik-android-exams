package org.stepik.android.exams.core.presenter.contracts

import android.view.ViewGroup
import org.stepik.android.exams.data.model.Step

interface StepsView {
    fun setHeader(stepViewContainer: ViewGroup, step: Step?)
    fun updateCommentState(stepViewContainer: ViewGroup, step: Step?)
}