package org.stepik.android.exams.ui.adapter

import android.graphics.drawable.Drawable
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.step_text_header.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.contracts.StepsView
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.ui.custom.LatexSupportableEnhancedFrameLayout
import org.stepik.android.exams.util.resolvers.StepTypeResolver

class StepPagerAdapter(
        val steps: List<Step>?,
        val stepTypeResolver: StepTypeResolver
) : PagerAdapter(), StepsView {

    override fun isViewFromObject(view: View?, obj: Any?) = view == obj

    override fun getCount(): Int = steps?.size ?: 0

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val inflater = LayoutInflater.from(container?.context)
        val stepViewContainer = inflater.inflate(R.layout.step_item_view, container, false) as ViewGroup
        val stepDelegate = stepTypeResolver.getStepDelegate(steps?.get(position))
        val view = stepDelegate.createView(stepViewContainer)
        stepViewContainer.addView(view)
        container?.addView(stepViewContainer)
        return stepViewContainer
    }

    fun getTabDrawable(position: Int): Drawable? {
        if (position >= steps?.size ?: 0) return null
        val step = steps?.get(position)
        return stepTypeResolver.getDrawableForType(step?.block?.name, step?.is_custom_passed
                ?: false, step?.actions?.doReview != null)
    }


    override fun setHeader(stepViewContainer: ViewGroup, step: Step?) {
        val header: LatexSupportableEnhancedFrameLayout = stepViewContainer.text_header
        header.setText(step?.block?.text)
        header.visibility = View.VISIBLE
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(`object` as View)
    }

}