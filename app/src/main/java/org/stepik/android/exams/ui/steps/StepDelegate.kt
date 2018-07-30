package org.stepik.android.exams.ui.steps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.step_text_header.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.ui.custom.LatexSupportableEnhancedFrameLayout

abstract class StepDelegate(var step : Step?) {

    protected open fun onCreateView(parent: ViewGroup) = LayoutInflater.from(parent.context).inflate(R.layout.step_delegate, parent, false)

    fun createView(parent: ViewGroup) =
            onCreateView(parent).also { onViewCreated(it) }

    protected open fun onViewCreated(view: View) = showHeader(view)

    private fun showHeader(view: View) {
        val header: LatexSupportableEnhancedFrameLayout = view.text_header
        header.setText(step?.block?.text)
        view.text_header.visibility = View.VISIBLE
    }
}