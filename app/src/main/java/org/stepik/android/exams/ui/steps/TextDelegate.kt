package org.stepik.android.exams.ui.steps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.stepik.android.exams.R

class TextDelegate : StepDelegate() {
    override fun onCreateView(parent: ViewGroup): View = LayoutInflater.from(parent.context).inflate(R.layout.delegate_text_step, parent, false)
}