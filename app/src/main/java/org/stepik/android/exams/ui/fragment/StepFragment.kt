package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.attempt_container_layout.view.*
import kotlinx.android.synthetic.main.next_lesson_view.*
import kotlinx.android.synthetic.main.next_lesson_view.view.*
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
    var id = ""

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
    }

    override fun moveToLesson(id: String, lesson: Lesson?) = screenManager.showStepsList(id, lesson
            ?: Lesson(), context)

    override fun hideNavigation() {
        if (step?.position == 1L)
            prevLesson.visibility = View.GONE
        if (step?.is_last == true)
            nextLesson.visibility = View.GONE
        route_lesson_root.visibility = View.GONE
    }

    override fun showNavigation() {
        if (step?.position == 1L)
            prevLesson.visibility = View.VISIBLE
        if (step?.is_last == true)
            nextLesson.visibility = View.VISIBLE
        route_lesson_root.visibility = View.VISIBLE
    }

    private fun loadNavigation() {
        nextLesson = view?.route_lesson_root?.next_lesson_view as TextView
        prevLesson = route_lesson_root.previous_lesson_view
        navigatePresenter.navigateToLesson(step, id, move = false)
        nextLesson.setOnClickListener { _ ->
            navigatePresenter.navigateToLesson(step, id, move = true)
        }
        prevLesson.setOnClickListener { _ ->
            navigatePresenter.navigateToLesson(step, id, move = true)
        }
    }

    private fun showHeader() {
        val header: LatexSupportableEnhancedFrameLayout?  = text_header
        header?.setText(step?.block?.text)
        text_header?.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance(step: Step?, id: String): StepFragment {
            val args = Bundle()
            args.putString("id", id)
            args.putParcelable("step", step)
            val fragment = StepFragment()
            fragment.arguments = args
            return fragment
        }
    }

}