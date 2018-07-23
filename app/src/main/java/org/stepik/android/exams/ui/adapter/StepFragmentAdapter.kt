package org.stepik.android.exams.ui.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.stepik.android.exams.R
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.util.resolvers.StepTypeResolver


class StepFragmentAdapter(var context: Context, val stepList: List<Step?>, val stepTypeResolver: StepTypeResolver) : PagerAdapter() {

    override fun isViewFromObject(view: View?, `object`: Any?) = view == `object`

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.delegate_text_step, container, false) as ViewGroup
        container?.addView(layout)
        return layout
    }

    override fun getCount(): Int {
        return stepList.size
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        super.destroyItem(container, position, `object`)
    }

    fun getTabDrawable(position: Int): Drawable? {
        if (position >= stepList.size) return null
        val step = stepList[position]
        return stepTypeResolver.getDrawableForType(step?.block?.name, step?.is_custom_passed
                ?: false)
    }
}
