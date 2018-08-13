package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_steps.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.BasePresenterFragment
import org.stepik.android.exams.core.presenter.ProgressPresenter
import org.stepik.android.exams.core.presenter.contracts.ProgressView
import org.stepik.android.exams.data.model.Lesson
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.ui.adapter.StepPagerAdapter
import org.stepik.android.exams.ui.listeners.RoutingViewListener
import org.stepik.android.exams.util.resolvers.StepTypeImpl
import org.stepik.android.exams.util.resolvers.StepTypeResolver
import javax.inject.Inject
import javax.inject.Provider


class StepListFragment : BasePresenterFragment<ProgressPresenter, ProgressView>(), RoutingViewListener, ProgressView {
    lateinit var stepTypeResolver: StepTypeResolver
    lateinit var adapter: StepPagerAdapter
    lateinit var steps: List<Step>
    @Inject
    lateinit var stepPresenterProvider: Provider<ProgressPresenter>

    override fun getPresenterProvider() = stepPresenterProvider

    override fun injectComponent() {
        App.componentManager().stepComponent.inject(this)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lesson: Lesson? = arguments.getParcelable("lesson")
        val id: String = arguments.getString("id", "")
        steps = lesson?.stepsList!!
        stepTypeResolver = StepTypeImpl(context)
        adapter = StepPagerAdapter(childFragmentManager, id, steps, stepTypeResolver)
        pagers.adapter = adapter
        tabs.setupWithViewPager(pagers)
        tabs.tabMode = TabLayout.MODE_SCROLLABLE
    }

    override fun onResume() {
        presenter?.attachView(this)
        presenter?.isAllStepsPassed(steps)
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
        pagers.currentItem = position + 1
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
        fun newInstance(id: String, lesson: Lesson): StepListFragment {
            val args = Bundle()
            args.putString("id", id)
            args.putParcelable("lesson", lesson)
            val fragment = StepListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}