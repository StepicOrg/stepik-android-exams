package org.stepik.android.exams.util.resolvers

import android.graphics.drawable.Drawable
import org.stepik.android.exams.ui.steps.StepDelegate
import org.stepik.android.model.Step

interface StepTypeResolver {

    fun getDrawableForType(type: String?, viewed: Boolean, isPeerReview: Boolean): Drawable?

    fun getStepDelegate(step: Step?): StepDelegate
}
