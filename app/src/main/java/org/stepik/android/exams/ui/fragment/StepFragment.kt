package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import org.stepik.android.exams.util.AppConstants
import org.stepik.android.exams.util.resolvers.StepTypeResolver
import org.stepik.android.exams.util.resolvers.StepTypeResolverImpl
import org.stepik.android.model.Step
import javax.inject.Inject
import javax.inject.Provider

open class StepFragment :
        BasePresenterFragment<StepAttemptPresenter, AttemptView>(),
        NavigateView,
        ProgressView {
    @Inject
    lateinit var stepPresenterProvider: Provider<StepAttemptPresenter>
    @Inject
    lateinit var navigationPresenter: NavigationPresenter
    @Inject
    lateinit var screenManager: ScreenManager
    @Inject
    protected lateinit var progressPresenter: ProgressPresenter
    protected var topicId = ""
    protected var lastPosition = 0

    override fun getPresenterProvider() = stepPresenterProvider

    override fun injectComponent() {
        App.componentManager().stepComponent.inject(this)
    }

    protected var step: Step? = null
    lateinit var stepTypeResolver: StepTypeResolver
    protected lateinit var parentContainer: ViewGroup
    protected lateinit var attemptContainer: ViewGroup
    private lateinit var nextLesson: TextView
    private lateinit var prevLesson: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        step = arguments.getParcelable(AppConstants.step)
        topicId = arguments.getString(AppConstants.topicId, "")
        lastPosition = arguments.getInt(AppConstants.lastPosition)
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

    override fun moveToLesson(id: String, lesson: LessonWrapper?) = screenManager.showStepsList(id, lesson!!, context)

    override fun showNextButton() {
        prevLesson.visibility = View.VISIBLE
        prevLesson.setOnClickListener { _ ->
            navigationPresenter.navigateToLesson(step, topicId, lastPosition, move = true)
        }
        route_lesson_root.visibility = View.VISIBLE
    }

    override fun hideNextButton() {
        nextLesson.visibility = View.GONE
        route_lesson_root.visibility = View.GONE
    }

    override fun showPrevButton() {
        nextLesson.visibility = View.VISIBLE
        nextLesson.setOnClickListener { _ ->
            navigationPresenter.navigateToLesson(step, topicId, lastPosition, move = true)
        }
        route_lesson_root.visibility = View.VISIBLE
    }

    override fun hidePrevButton() {
        prevLesson.visibility = View.GONE
        route_lesson_root.visibility = View.GONE
    }

    private fun loadNavigation() {
        nextLesson = next_lesson_view
        prevLesson = previous_lesson_view
        navigationPresenter.navigateToLesson(step, topicId, lastPosition, move = false)
    }

    private fun showHeader() {
        text_header?.setText(step?.block?.text)
        text_header?.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance(step: Step?, topicId: String, lastPosition: Int): StepFragment {
            val args = Bundle()
            args.putString(AppConstants.topicId, topicId)
            args.putParcelable(AppConstants.step, step)
            args.putInt(AppConstants.lastPosition, lastPosition)
            val fragment = StepFragment()
            fragment.arguments = args
            return fragment
        }
    }

}