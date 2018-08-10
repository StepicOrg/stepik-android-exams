package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.attempt_container_layout.view.*
import kotlinx.android.synthetic.main.next_lesson_view.view.*
import kotlinx.android.synthetic.main.step_text_header.view.*
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
import org.stepik.android.exams.data.model.Lesson
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.ui.custom.LatexSupportableEnhancedFrameLayout
import org.stepik.android.exams.util.resolvers.StepTypeImpl
import org.stepik.android.exams.util.resolvers.StepTypeResolver
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
        stepTypeResolver = StepTypeImpl(context)
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

    override fun markedAsView(steps: MutableList<Step>) {
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
        showHeader(view)
        showNavigation(view)
    }

    override fun moveToLesson(lesson: Lesson?) = screenManager.showStepsList(lesson
            ?: Lesson(), context)

    private fun showNavigation(view: View?) {
        nextLesson = view?.route_lesson_root?.next_lesson_view as TextView
        prevLesson = view.route_lesson_root.previous_lesson_view
        if (step?.position == 1L)
            prevLesson.visibility = View.VISIBLE
        if (step?.is_last == true)
            nextLesson.visibility = View.VISIBLE
        view.route_lesson_root.visibility = View.VISIBLE
        nextLesson.setOnClickListener { _ ->
            navigatePresenter.navigateToLesson(step)
        }
        prevLesson.setOnClickListener { _ ->
            navigatePresenter.navigateToLesson(step)
        }
    }


    private fun showHeader(view: View?) {
        val header: LatexSupportableEnhancedFrameLayout? = view?.text_header
        header?.setText(step?.block?.text)
        view?.text_header?.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance(step: Step?): StepFragment {
            val args = Bundle()
            args.putParcelable("step", step)
            val fragment = StepFragment()
            fragment.arguments = args
            return fragment
        }
    }

}