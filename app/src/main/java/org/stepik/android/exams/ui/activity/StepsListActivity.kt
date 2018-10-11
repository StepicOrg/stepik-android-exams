package org.stepik.android.exams.ui.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.MenuItem
import kotlinx.android.synthetic.main.steps_activity.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.BasePresenterActivity
import org.stepik.android.exams.core.presenter.ProgressPresenter
import org.stepik.android.exams.core.presenter.contracts.ProgressView
import org.stepik.android.exams.data.model.LessonTheoryWrapper
import org.stepik.android.exams.ui.adapter.StepPagerAdapter
import org.stepik.android.exams.ui.listeners.RoutingViewListener
import org.stepik.android.exams.util.initCenteredToolbar
import org.stepik.android.exams.util.resolvers.StepTypeResolver
import org.stepik.android.model.Step
import javax.inject.Inject
import javax.inject.Provider

class StepsListActivity : BasePresenterActivity<ProgressPresenter, ProgressView>(), RoutingViewListener, ProgressView {
    companion object {
        const val EXTRA_LESSON = "lesson"
        const val EXTRA_TOPIC_ID = "topicId"
        const val EXTRA_COURSE = "course"
    }

    private lateinit var adapter: StepPagerAdapter
    private lateinit var steps: List<Step>

    @Inject
    lateinit var stepPresenterProvider: Provider<ProgressPresenter>

    @Inject
    lateinit var stepTypeResolver: StepTypeResolver

    private lateinit var pageChangeListener: ViewPager.OnPageChangeListener

    override fun getPresenterProvider() =
            stepPresenterProvider

    override fun injectComponent() {
        App.componentManager().stepComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.steps_activity)

        val lessonTheory: LessonTheoryWrapper = intent.getParcelableExtra(EXTRA_LESSON)
        val topicId: String = intent.getStringExtra(EXTRA_TOPIC_ID)
        val course = intent.getLongExtra(EXTRA_COURSE, 0L)

        val lessonName = lessonTheory.lesson.title
        initCenteredToolbar(lessonName ?: getString(R.string.theory), showHomeButton = true)

        steps = lessonTheory.stepsList

        adapter = StepPagerAdapter(supportFragmentManager, topicId, steps, stepTypeResolver)
        pagers.adapter = adapter
        pageChangeListener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                presenter?.stepPassedLocal(steps[position], course)
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

    override fun onOptionsItemSelected(item: MenuItem?) =
            if (item?.itemId == android.R.id.home) {
                onBackPressed()
                true
            } else {
                super.onOptionsItemSelected(item)
            }
}