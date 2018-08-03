package org.stepik.android.exams.ui.steps

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import org.stepik.android.exams.data.model.Reply
import org.stepik.android.exams.data.model.Submission
import org.stepik.android.exams.data.model.attempts.Attempt

abstract class StepDelegate {
    abstract fun onCreateView(parent: ViewGroup): View
    protected open fun onViewCreated(view: View) {}
    fun createView(parent: ViewGroup) =
            onCreateView(parent).also { onViewCreated(it) }
}