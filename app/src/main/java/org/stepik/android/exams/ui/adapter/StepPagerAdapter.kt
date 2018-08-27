package org.stepik.android.exams.ui.adapter

import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import org.stepik.android.exams.ui.fragment.AttemptFragment
import org.stepik.android.exams.ui.fragment.StepFragment
import org.stepik.android.exams.util.AppConstants
import org.stepik.android.exams.util.resolvers.StepTypeResolver
import org.stepik.android.model.Step

class StepPagerAdapter(
        fragmentManager: FragmentManager,
        private val topicId: String,
        stepsList: List<Step>,
        private val stepTypeResolver: StepTypeResolver
) : FragmentPagerAdapter(fragmentManager) {
    private var steps: MutableList<Step> = stepsList.toMutableList()
    override fun getItem(position: Int): Fragment {
        return when (steps.getOrNull(position)?.block?.name) {
            AppConstants.TYPE_TEXT -> StepFragment.newInstance(steps[position], topicId, lastPosition = steps.size.toLong())
            else -> AttemptFragment.newInstance(steps[position], topicId, lastPosition = steps.size.toLong())
        }
    }

    fun updateSteps(steps: List<Step>?) {
        steps?.let {
            this.steps = steps.toMutableList()
            notifyDataSetChanged()
        }
    }

    fun updateStep(step: Step, position: Int) {
        steps[position] = step
    }

    override fun getCount(): Int = steps.size

    fun getTabDrawable(position: Int): Drawable? {
        position.let {
            val step = steps.getOrNull(position)
            return stepTypeResolver.getDrawableForType(step?.block?.name, step?.isCustomPassed
                    ?: false,
                    step?.actions?.doReview != null)
        }
    }

}