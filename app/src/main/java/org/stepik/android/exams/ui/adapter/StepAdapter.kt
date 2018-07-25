package org.stepik.android.exams.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.recycler_item.view.*
import kotlinx.android.synthetic.main.step_item_view.view.*
import kotlinx.android.synthetic.main.step_text_header.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.StepsPresenter
import org.stepik.android.exams.core.presenter.contracts.StepsView
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.ui.custom.LatexSupportableEnhancedFrameLayout
import org.stepik.android.exams.ui.steps.TextDelegate
import org.stepik.android.exams.util.resolvers.StepTypeResolver

class StepAdapter(var context: Context, val steps: List<Step>?) : RecyclerView.Adapter<StepAdapter.StepViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            StepViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.step_item_view, parent, false))


    override fun getItemCount() = steps?.size ?: 0


    override fun onBindViewHolder(holder: StepViewHolder?, position: Int) {
        holder?.bind(StepsPresenter(steps?.get(position)))
    }


    class StepViewHolder (
            root: View
    ): RecyclerView.ViewHolder(root), StepsView {
        private var stepDelegate = TextDelegate()
        private val stepViewContainer: ViewGroup = root.container
        private lateinit var stepTypeResolver : StepTypeResolver
        private var presenter :StepsPresenter? = null

        override fun initialize(){
            stepViewContainer.removeAllViews()
            val view = stepDelegate.createView(stepViewContainer)
            stepViewContainer.addView(view)
        }

        fun bind(presenter: StepsPresenter) {
            this.presenter = presenter
            presenter.attachView(this)
        }
            //stepDelegate = stepTypeResolver.getStepDelegate(step)
        override fun setHeader(step: Step?){
            val header : LatexSupportableEnhancedFrameLayout = stepViewContainer.container.text_header
                header.setText(step?.block?.text)
            header.visibility = View.VISIBLE
        }
    }
}