package org.stepik.android.exams.ui.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.open_comment_view.view.*
import kotlinx.android.synthetic.main.step_text_header.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.ui.custom.LatexSupportableEnhancedFrameLayout
import org.stepik.android.exams.ui.steps.TextDelegate

class StepPagerAdapter(val context : Context, val steps : List<Step>?) : PagerAdapter() {
    override fun isViewFromObject(view: View?, obj: Any?) = view == obj

    override fun getCount(): Int =
        steps?.size ?: 0

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val stepDelegate = TextDelegate()
        val inflater = LayoutInflater.from(context)
        val stepViewContainer = inflater.inflate(R.layout.step_item_view, container, false) as ViewGroup
        val view = stepDelegate.createView(stepViewContainer)
        stepViewContainer.addView(view)
        setHeader(stepViewContainer, steps?.get(position))
        container?.addView(stepViewContainer)
        return stepViewContainer
    }
    fun setHeader(stepViewContainer: ViewGroup, step: Step?){
        val header : LatexSupportableEnhancedFrameLayout = stepViewContainer.text_header
        header.setText(step?.block?.text)
        header.visibility = View.VISIBLE
        val comments : TextView = stepViewContainer.open_comments_text
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(`object` as View)
    }

}