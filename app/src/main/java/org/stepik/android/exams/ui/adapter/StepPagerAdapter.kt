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
        stepsList: List<Step>?,
        private val stepTypeResolver: StepTypeResolver
) : FragmentPagerAdapter(fragmentManager) {
    private var steps: MutableList<Step> = stepsList as MutableList<Step>
    override fun getItem(position: Int): Fragment {
        return when (steps.getOrNull(position)?.block?.name) {
            "text" -> StepFragment.newInstance(steps.getOrNull(position))
            else -> AttemptFragment.newInstance(steps.getOrNull(position))
        }
    }

    fun updateSteps(steps: MutableList<Step>?) {
        steps?.let {
            this.steps = steps
            notifyDataSetChanged()
        }
    }

    fun updateStep(step: Step, position: Int) {
        steps[position] = step
    }

    override fun getCount(): Int = steps.size
    fun getTabDrawable(position: Int): Drawable? {
        if (position >= steps.size) return null
        val step = steps[position]
        return stepTypeResolver.getDrawableForType(step.block?.name, step.is_custom_passed,
                step.actions?.doReview != null)
    }

}