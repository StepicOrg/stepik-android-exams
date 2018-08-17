package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_steps.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.BasePresenterFragment
import org.stepik.android.exams.core.presenter.ProgressPresenter
import org.stepik.android.exams.core.presenter.contracts.ProgressView
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.ui.adapter.StepPagerAdapter
import org.stepik.android.exams.ui.listeners.RoutingViewListener
import org.stepik.android.exams.util.AppConstants
import org.stepik.android.exams.util.resolvers.StepTypeResolver
import org.stepik.android.model.Step
import javax.inject.Inject
import javax.inject.Provider


class StepListFragment : BasePresenterFragment<ProgressPresenter, ProgressView>(), RoutingViewListener, ProgressView {
    private lateinit var adapter: StepPagerAdapter
    private lateinit var steps: List<Step>
    @Inject
    lateinit var stepPresenterProvider: Provider<ProgressPresenter>
    @Inject
    lateinit var stepTypeResolver: StepTypeResolver
    private lateinit var pageChangeListener: ViewPager.OnPageChangeListener
    override fun getPresenterProvider() = stepPresenterProvider

    override fun injectComponent() {
        App.componentManager().stepComponent.inject(this)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lesson: LessonWrapper? = arguments.getParcelable(AppConstants.lesson)
        val topicId: String = arguments.getString(AppConstants.topicId, "")
        steps = lesson?.stepsList!!
        adapter = StepPagerAdapter(childFragmentManager, topicId, steps, stepTypeResolver)
        pagers.adapter = adapter
        pageChangeListener = object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                presenter?.stepPassedLocal(steps[position])
            }
        }
        pagers.addOnPageChangeListener(pageChangeListener)
        tabs.setupWithViewPager(pagers)
        tabs.tabMode = TabLayout.MODE_SCROLLABLE
    }

    override fun onResume() {
        presenter?.attachView(this)
        presenter?.isAllStepsPassed(steps)
        pageChangeListener.onPageSelected(0)
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter?.detachView(this)
    }

    override fun markedAsView(step: Step) {
        setTab(step)
    }

    override fun markedAsView(steps: List<Step>) {
        setTabs(steps)
    }

    private fun setTab(step: Step) {
        val pos = (step.position - 1).toInt()
        val tab = tabs.getTabAt(pos)
        adapter.updateStep(step, pos)
        tab?.icon = adapter.getTabDrawable(pos)
    }

    override fun scrollNext(position: Int) {
        pagers.currentItem = position
    }

    private fun setTabs(steps: List<Step>?) {
        adapter.updateSteps(steps)
        steps?.forEachIndexed { index, _ ->
            val tab = tabs.getTabAt(index)
            tab?.icon = adapter.getTabDrawable(index)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater?.inflate(R.layout.fragment_steps, container, false)

    companion object {
        fun newInstance(topicId: String, lesson: LessonWrapper): StepListFragment {
            val args = Bundle()
            args.putString(AppConstants.topicId, topicId)
            args.putParcelable(AppConstants.lesson, lesson)
            val fragment = StepListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}