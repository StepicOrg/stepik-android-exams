package org.stepik.android.exams.util.resolvers

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.DrawableRes
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.ui.steps.*
import org.stepik.android.exams.util.AppConstants
import javax.inject.Inject

class StepTypeImpl
@Inject
constructor(
        private val context: Context
) : StepTypeResolver {
    override fun getDrawableForType(type: String?, viewed: Boolean): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getDrawable(@DrawableRes drawableRes: Int): Drawable? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(drawableRes)
        } else {
            context.resources.getDrawable(drawableRes)
        }
    }

    override fun getStep(step: Step?): StepDelegate {
        val errorDelegate = NotSupportedDelegate()
        if (step?.block.toString().isEmpty())
            return errorDelegate

        val type = step?.block?.name
        return when (type) {
            AppConstants.TYPE_VIDEO -> VideoDelegate()
            AppConstants.TYPE_STRING -> StringDelegate()
            AppConstants.TYPE_NUMBER -> NumberDelegate()
            AppConstants.TYPE_TEXT -> TextDelegate()
            AppConstants.TYPE_CHOICE -> ChoiceDelegate()
            AppConstants.TYPE_FREE_ANSWER -> FreeAnswerDelegate()
            else -> errorDelegate
        }
    }
}