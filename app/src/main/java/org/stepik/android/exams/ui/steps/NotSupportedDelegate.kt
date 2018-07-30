package org.stepik.android.exams.ui.steps

import android.view.View
import android.view.ViewGroup
import org.stepik.android.exams.data.model.Step

class NotSupportedDelegate(step : Step?) : StepDelegate(step) {
    override fun onCreateView(parent: ViewGroup): View = View(parent.context)
}