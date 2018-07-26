package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.data.model.Step

interface StepsView {
    fun setHeader(step: Step?)
    fun initialize()
    fun destroy()
}