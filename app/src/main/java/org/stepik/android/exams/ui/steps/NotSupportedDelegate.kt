package org.stepik.android.exams.ui.steps

import android.view.View
import android.view.ViewGroup

class NotSupportedDelegate : StepDelegate() {
    override fun onCreateView(parent: ViewGroup): View = View(parent.context)
}