package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_steps.*
import org.stepik.android.exams.R
import org.stepik.android.exams.ui.listeners.RoutingViewListener
import org.stepik.android.exams.data.model.Lesson
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.ui.adapter.StepPagerAdapter
import org.stepik.android.exams.util.resolvers.StepTypeImpl
import org.stepik.android.exams.util.resolvers.StepTypeResolver


class StepsFragment : Fragment(), RoutingViewListener {

    lateinit var stepTypeResolver: StepTypeResolver

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stepTypeResolver = StepTypeImpl(context)
        val lesson: Lesson = arguments.getParcelable("lesson")
        val stepAdapter = StepPagerAdapter(lesson.stepsList, stepTypeResolver, this)
        pagers.adapter = stepAdapter
        tabs.setupWithViewPager(pagers)
        tabs.tabMode = TabLayout.MODE_SCROLLABLE
        setTabs(lesson.stepsList, stepAdapter)
    }

    override fun scrollNext(position: Int) {
        pagers.currentItem = position + 1
    }

    private fun setTabs(steps: List<Step>?, stepPagerAdapter: StepPagerAdapter) {
        steps?.forEachIndexed { index, _ ->
            val tab = tabs.getTabAt(index)
            tab?.icon = stepPagerAdapter.getTabDrawable(index)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater?.inflate(R.layout.fragment_steps, container, false)

    companion object {
        fun newInstance(lesson: Lesson): StepsFragment {
            val args = Bundle()
            args.putParcelable("lesson", lesson)
            val fragment = StepsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}