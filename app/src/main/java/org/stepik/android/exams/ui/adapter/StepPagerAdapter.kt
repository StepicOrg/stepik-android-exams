package org.stepik.android.exams.ui.adapter

import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.ui.fragment.AttemptFragment
import org.stepik.android.exams.ui.fragment.StepFragment
import org.stepik.android.exams.util.resolvers.StepTypeResolver

class StepPagerAdapter(
        fragmentManager: FragmentManager,
        val steps: List<Step>?,
        val stepTypeResolver: StepTypeResolver
) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return when (steps?.getOrNull(position)?.block?.name) {
            "text" -> StepFragment.newInstance(steps.getOrNull(position))
            else -> AttemptFragment.newInstance(steps?.getOrNull(position))
        }
    }

    override fun getCount(): Int = steps?.size ?: 0
    fun getTabDrawable(position: Int): Drawable? {
        if (position >= steps?.size ?: 0) return null
        val step = steps?.get(position)
        return stepTypeResolver.getDrawableForType(step?.block?.name, step?.is_custom_passed
                ?: false, step?.actions?.doReview != null)
    }

}