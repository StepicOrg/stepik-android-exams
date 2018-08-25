package org.stepik.android.exams.util.resolvers

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import org.stepik.android.exams.R
import org.stepik.android.exams.ui.steps.*
import org.stepik.android.exams.util.AppConstants
import org.stepik.android.model.Step
import java.util.*
import javax.inject.Inject

class StepTypeResolverImpl
@Inject
constructor(
        private val context: Context
) : StepTypeResolver {
    private val mapFromTypeToDrawable: MutableMap<String, Drawable?>
    private val mapFromTypeToDrawableNotViewed: MutableMap<String, Drawable?>
    private val peerReviewDrawable: Drawable?
    private val peerReviewDrawableNotViewed: Drawable?

    init {
        mapFromTypeToDrawable = HashMap()
        mapFromTypeToDrawableNotViewed = HashMap()

        peerReviewDrawableNotViewed = getNotViewedDrawable(getDrawable(context, R.drawable.ic_peer_review))
        peerReviewDrawable = getViewedDrawable(getDrawable(context, R.drawable.ic_peer_review)?.mutate())

        val simpleQuestionDrawableNotViewed = getNotViewedDrawable(getDrawable(context, R.drawable.ic_easy_quiz))
        val simpleQuestionDrawable = getViewedDrawable(getDrawable(context, R.drawable.ic_easy_quiz)?.mutate())

        val videoDrawableNotViewed = getNotViewedDrawable(getDrawable(context, R.drawable.ic_video_pin))
        val videoDrawable = getViewedDrawable(getDrawable(context, R.drawable.ic_video_pin)?.mutate())

        val animationDrawableNotViewed = getNotViewedDrawable(getDrawable(context, R.drawable.ic_animation))
        val animationDrawable = getViewedDrawable(getDrawable(context, R.drawable.ic_animation)?.mutate())

        val hardQuizDrawableNotViewed = getNotViewedDrawable(getDrawable(context, R.drawable.ic_hard_quiz))
        val hardQuizDrawable = getViewedDrawable(getDrawable(context, R.drawable.ic_hard_quiz)?.mutate())

        val theoryDrawableNotViewed = getNotViewedDrawable(getDrawable(context, R.drawable.ic_theory))
        val theoryQuizDrawable = getViewedDrawable(getDrawable(context, R.drawable.ic_theory)?.mutate())

        mapFromTypeToDrawable[AppConstants.TYPE_TEXT] = theoryQuizDrawable
        mapFromTypeToDrawable[AppConstants.TYPE_VIDEO] = videoDrawable
        mapFromTypeToDrawable[AppConstants.TYPE_MATCHING] = simpleQuestionDrawable
        mapFromTypeToDrawable[AppConstants.TYPE_SORTING] = simpleQuestionDrawable
        mapFromTypeToDrawable[AppConstants.TYPE_FREE_ANSWER] = simpleQuestionDrawable
        mapFromTypeToDrawable[AppConstants.TYPE_TABLE] = simpleQuestionDrawable
        mapFromTypeToDrawable[AppConstants.TYPE_STRING] = simpleQuestionDrawable
        mapFromTypeToDrawable[AppConstants.TYPE_CHOICE] = simpleQuestionDrawable
        mapFromTypeToDrawable[AppConstants.TYPE_NUMBER] = simpleQuestionDrawable
        mapFromTypeToDrawable[AppConstants.TYPE_FILL_BLANKS] = simpleQuestionDrawable


        mapFromTypeToDrawableNotViewed[AppConstants.TYPE_TEXT] = theoryDrawableNotViewed
        mapFromTypeToDrawableNotViewed[AppConstants.TYPE_VIDEO] = videoDrawableNotViewed
        mapFromTypeToDrawableNotViewed[AppConstants.TYPE_MATCHING] = simpleQuestionDrawableNotViewed
        mapFromTypeToDrawableNotViewed[AppConstants.TYPE_SORTING] = simpleQuestionDrawableNotViewed
        mapFromTypeToDrawableNotViewed[AppConstants.TYPE_FREE_ANSWER] = simpleQuestionDrawableNotViewed
        mapFromTypeToDrawableNotViewed[AppConstants.TYPE_TABLE] = simpleQuestionDrawableNotViewed
        mapFromTypeToDrawableNotViewed[AppConstants.TYPE_STRING] = simpleQuestionDrawableNotViewed
        mapFromTypeToDrawableNotViewed[AppConstants.TYPE_CHOICE] = simpleQuestionDrawableNotViewed
        mapFromTypeToDrawableNotViewed[AppConstants.TYPE_NUMBER] = simpleQuestionDrawableNotViewed
        mapFromTypeToDrawableNotViewed[AppConstants.TYPE_FILL_BLANKS] = simpleQuestionDrawableNotViewed
    }

    override fun getDrawableForType(type: String?, viewed: Boolean, isPeerReview: Boolean): Drawable? {
        if (isPeerReview) {
            return if (viewed) {
                peerReviewDrawable
            } else {
                peerReviewDrawableNotViewed
            }
        }

        if (viewed) {
            var drawable: Drawable? = mapFromTypeToDrawable[type]
            if (drawable == null) {
                drawable = mapFromTypeToDrawable[AppConstants.TYPE_TEXT]
            }

            return drawable
        } else {
            var drawable: Drawable? = mapFromTypeToDrawableNotViewed[type]
            if (drawable == null) {
                drawable = mapFromTypeToDrawableNotViewed[AppConstants.TYPE_TEXT]
            }
            return drawable
        }
    }

    private fun getDrawable(@DrawableRes drawableRes: Int): Drawable? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(drawableRes)
        } else {
            context.resources.getDrawable(drawableRes)
        }
    }

    private fun getNotViewedDrawable(drawable: Drawable?): Drawable? {
        return getDrawableWithColor(drawable, R.color.unviewed_step)

    }

    private fun getDrawableWithColor(drawable: Drawable?, @ColorRes colorRes: Int): Drawable? {
        @ColorInt
        val colorToSet = ContextCompat.getColor(context, colorRes)
        val mMode = PorterDuff.Mode.SRC_ATOP
        drawable?.setColorFilter(colorToSet, mMode)
        return drawable
    }

    private fun getViewedDrawable(drawable: Drawable?): Drawable? {
        return getDrawableWithColor(drawable, R.color.viewed_step)
    }

    private fun getDrawable(context: Context, @DrawableRes drawableRes: Int): Drawable? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(drawableRes)
        } else {
            context.resources.getDrawable(drawableRes)
        }
    }

    override fun getStepDelegate(step: Step?): StepDelegate {
        val errorDelegate = NotSupportedDelegate()
        if (step?.block.toString().isEmpty())
            return errorDelegate

        val type = step?.block?.name
        return when (type) {
            AppConstants.TYPE_STRING -> StringDelegate()
            AppConstants.TYPE_NUMBER -> NumberDelegate()
            AppConstants.TYPE_CHOICE -> ChoiceDelegate()
            AppConstants.TYPE_FREE_ANSWER -> FreeAnswerDelegate()
            AppConstants.TYPE_TABLE -> TableChoiceDelegate()
            AppConstants.TYPE_TEXT -> TextDelegate()
            else -> errorDelegate
        }
    }
}