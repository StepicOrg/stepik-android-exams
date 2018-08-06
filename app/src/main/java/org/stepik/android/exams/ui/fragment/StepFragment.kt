package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.attempt_container_layout.view.*
import kotlinx.android.synthetic.main.step_text_header.view.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.BasePresenterFragment
import org.stepik.android.exams.core.presenter.StepAttemptPresenter
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.ui.custom.LatexSupportableEnhancedFrameLayout
import org.stepik.android.exams.util.resolvers.StepTypeImpl
import org.stepik.android.exams.util.resolvers.StepTypeResolver
import javax.inject.Inject
import javax.inject.Provider

open class StepFragment : BasePresenterFragment<StepAttemptPresenter, AttemptView>() {
    @Inject
    lateinit var stepPresenterProvider: Provider<StepAttemptPresenter>

    override fun getPresenterProvider() = stepPresenterProvider

    override fun injectComponent() {
        App.component().inject(this)
    }

    protected lateinit var step: Step
    lateinit var stepTypeResolver: StepTypeResolver
    protected lateinit var parentContainer: ViewGroup
    protected lateinit var attemptContainer: ViewGroup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        step = arguments.getParcelable("step")
        stepTypeResolver = StepTypeImpl(context)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.step_delegate, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        parentContainer = view as ViewGroup
        attemptContainer = parentContainer.attempt_container as ViewGroup
        showHeader(view)
    }


    private fun showHeader(view: View?) {
        val header: LatexSupportableEnhancedFrameLayout? = view?.text_header
        header?.setText(step.block?.text)
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