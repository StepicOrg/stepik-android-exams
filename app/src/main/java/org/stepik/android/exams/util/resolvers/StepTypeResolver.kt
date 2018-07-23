package org.stepik.android.exams.util.resolvers

import android.graphics.drawable.Drawable
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.ui.steps.StepDelegate

interface StepTypeResolver {

    fun getDrawableForType(type: String?, viewed: Boolean): Drawable

    fun getStep(step: Step?): StepDelegate
}
