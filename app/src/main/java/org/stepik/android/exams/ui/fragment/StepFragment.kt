package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.attempt_container_layout.*
import kotlinx.android.synthetic.main.next_lesson_view.*
import kotlinx.android.synthetic.main.step_delegate.*
import kotlinx.android.synthetic.main.step_text_header.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.BasePresenterFragment
import org.stepik.android.exams.core.presenter.NavigationPresenter
import org.stepik.android.exams.core.presenter.ProgressPresenter
import org.stepik.android.exams.core.presenter.StepAttemptPresenter
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.core.presenter.contracts.NavigateView
import org.stepik.android.exams.core.presenter.contracts.ProgressView
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.util.argument
import org.stepik.android.exams.util.resolvers.StepTypeResolver
import org.stepik.android.exams.util.resolvers.StepTypeResolverImpl
import org.stepik.android.model.Step
import javax.inject.Inject
import javax.inject.Provider

open class StepFragment : BasePresenterFragment<StepAttemptPresenter, AttemptView>(), NavigateView, ProgressView {
    companion object {
        fun newInstance(step: Step, topicId: String, lastPosition: Long): StepFragment =
                StepFragment().apply {
                    this.step = step
                    this.topicId = topicId
                    this.lastPosition = lastPosition
                }
    }

    @Inject
    lateinit var stepPresenterProvider: Provider<StepAttemptPresenter>

    @Inject
    lateinit var navigationPresenter: NavigationPresenter

    @Inject
    lateinit var screenManager: ScreenManager

    @Inject
    protected lateinit var progressPresenter: ProgressPresenter

    private var step: Step by argument()
    private var topicId: String by argument()
    private var lastPosition: Long by argument()

    lateinit var stepTypeResolver: StepTypeResolver

    protected lateinit var parentContainer: ViewGroup
    protected lateinit var attemptContainer: ViewGroup

    override fun getPresenterProvider() = stepPresenterProvider

    override fun injectComponent() {
        App.componentManager().stepComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stepTypeResolver = StepTypeResolverImpl(context)
    }

    override fun markedAsView(step: Step) {
        val fragment = activity as ProgressView
        fragment.markedAsView(step)
    }

    override fun markedAsView(steps: List<Step>) {
        val fragment = activity as ProgressView
        fragment.markedAsView(steps)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater?.inflate(R.layout.step_delegate, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        parentContainer = view as ViewGroup
        attemptContainer = attempt_container
        showHeader()
        loadNavigation()
        swipeRefreshAttempt.isEnabled = false
        swipeRefreshAttempt.isRefreshing = false
    }

    override fun onStart() {
        super.onStart()
        navigationPresenter.attachView(this)
        progressPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        navigationPresenter.detachView(this)
        progressPresenter.detachView(this)
    }

    override fun moveToLesson(id: String, lesson: LessonWrapper?) =
            screenManager.showStepsList(id, lesson!!, context)

    override fun showNextButton() {
        previousLesson.visibility = View.VISIBLE
        previousLesson.setOnClickListener {
            navigationPresenter.navigateToLesson(step, topicId, lastPosition, move = true)
        }
        routeLesson.visibility = View.VISIBLE
    }

    override fun hideNextButton() {
        nextLesson.visibility = View.GONE
        routeLesson.visibility = View.GONE
    }

    override fun showPrevButton() {
        nextLesson.visibility = View.VISIBLE
        nextLesson.setOnClickListener {
            navigationPresenter.navigateToLesson(step, topicId, lastPosition, move = true)
        }
        routeLesson.visibility = View.VISIBLE
    }

    override fun hidePrevButton() {
        previousLesson.visibility = View.GONE
        routeLesson.visibility = View.GONE
    }

    private fun loadNavigation() {
        navigationPresenter.navigateToLesson(step, topicId, lastPosition, move = false)
    }

    private fun showHeader() {
        textHeader.setText(step.block?.text)
        textHeader.visibility = View.VISIBLE
    }
}