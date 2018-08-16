package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.attempt_container_layout.view.*
import kotlinx.android.synthetic.main.next_lesson_view.*
import kotlinx.android.synthetic.main.next_lesson_view.view.*
import kotlinx.android.synthetic.main.step_delegate.*
import kotlinx.android.synthetic.main.step_text_header.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.BasePresenterFragment
import org.stepik.android.exams.core.presenter.NavigatePresenter
import org.stepik.android.exams.core.presenter.ProgressPresenter
import org.stepik.android.exams.core.presenter.StepAttemptPresenter
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.core.presenter.contracts.NavigateView
import org.stepik.android.exams.core.presenter.contracts.ProgressView
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.ui.custom.LatexSupportableEnhancedFrameLayout
import org.stepik.android.exams.util.resolvers.StepTypeResolver
import org.stepik.android.exams.util.resolvers.StepTypeResolverImpl
import org.stepik.android.model.Lesson
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
    lateinit var navigatePresenter: NavigatePresenter
    @Inject
    lateinit var screenManager: ScreenManager
    @Inject
    protected lateinit var progressPresenter: ProgressPresenter
    protected var id = ""
    protected var lastPosition  = 0

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
        step = arguments.getParcelable("step")
        id = arguments.getString("id", "")
        lastPosition = arguments.getInt("last_position")
        stepTypeResolver = StepTypeResolverImpl(context)
    }

    override fun onStart() {
        super.onStart()
        navigatePresenter.attachView(this)
        progressPresenter.attachView(this)
    }

    override fun markedAsView(step: Step) {
        val fragment = parentFragment as ProgressView
        fragment.markedAsView(step)
    }

    override fun markedAsView(steps: List<Step>) {
        val fragment = parentFragment as ProgressView
        fragment.markedAsView(steps)
    }

    override fun onStop() {
        super.onStop()
        navigatePresenter.detachView(this)
        progressPresenter.detachView(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.step_delegate, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        parentContainer = view as ViewGroup
        attemptContainer = parentContainer.attempt_container as ViewGroup
        showHeader()
        loadNavigation()
        swipeRefreshAttempt.isEnabled = false
        swipeRefreshAttempt.isRefreshing = false
    }

    override fun moveToLesson(id: String, lesson: LessonWrapper?) = screenManager.showStepsList(id, lesson!!, context)

    override fun hideNavigation() {
        when (step?.position){
            1L -> prevLesson.visibility = View.GONE
            lastPosition.toLong() -> nextLesson.visibility = View.GONE
            else -> return
        }
        route_lesson_root.visibility = View.GONE
    }

    override fun showNavigation() {
        when (step?.position){
            1L -> prevLesson.visibility = View.VISIBLE
            lastPosition.toLong() -> nextLesson.visibility = View.VISIBLE
            else -> return
        }
        route_lesson_root.visibility = View.VISIBLE
    }

    private fun loadNavigation() {
        nextLesson = route_lesson_root.next_lesson_view
        prevLesson = route_lesson_root.previous_lesson_view
        navigatePresenter.navigateToLesson(step, id, lastPosition, move = false)
        nextLesson.setOnClickListener { _ ->
            navigatePresenter.navigateToLesson(step, id, lastPosition, move = true)
        }
        prevLesson.setOnClickListener { _ ->
            navigatePresenter.navigateToLesson(step, id, lastPosition, move = true)
        }
    }

    private fun showHeader() {
        val header: LatexSupportableEnhancedFrameLayout? = text_header
        header?.setText(step?.block?.text)
        text_header?.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance(step: Step?, id: String, lastPosition : Int): StepFragment {
            val args = Bundle()
            args.putString("id", id)
            args.putParcelable("step", step)
            args.putInt("last_position", lastPosition)
            val fragment = StepFragment()
            fragment.arguments = args
            return fragment
        }
    }

}