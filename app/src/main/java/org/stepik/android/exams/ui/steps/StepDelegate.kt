package org.stepik.android.exams.ui.steps
import android.view.View
import android.view.ViewGroup

abstract class StepDelegate {

    abstract fun onCreateView(parent: ViewGroup) : View

    protected open fun onViewCreated(view: View) {}
    fun createView(parent: ViewGroup) =
            onCreateView(parent).also { onViewCreated(it) }
}