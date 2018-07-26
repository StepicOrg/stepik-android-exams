package org.stepik.android.exams.ui.adapter

import android.app.Activity
import android.graphics.drawable.Drawable
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.open_comment_view.view.*
import kotlinx.android.synthetic.main.step_text_header.view.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.StepsPresenter
import org.stepik.android.exams.core.presenter.contracts.StepsView
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.ui.custom.LatexSupportableEnhancedFrameLayout
import org.stepik.android.exams.ui.steps.TextDelegate
import org.stepik.android.exams.util.resolvers.StepTypeResolver
import javax.inject.Inject

class StepPagerAdapter(val context: Activity, val steps: List<Step>?, val stepTypeResolver: StepTypeResolver) : PagerAdapter(), StepsView {
    @Inject
    lateinit var screenManager: ScreenManager

    private lateinit var presenter: StepsPresenter

    override fun isViewFromObject(view: View?, obj: Any?) = view == obj

    override fun getCount(): Int = steps?.size ?: 0

    init {
        App.component().inject(this)
    }

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val stepDelegate = TextDelegate()
        val inflater = LayoutInflater.from(context)
        val stepViewContainer = inflater.inflate(R.layout.step_item_view, container, false) as ViewGroup
        val view = stepDelegate.createView(stepViewContainer) // create view based on step type
        stepViewContainer.addView(view)
        presenter = StepsPresenter(stepViewContainer, steps?.get(position))
        attachView()
        presenter.fillTextDelegate() //fill delegate
        container?.addView(stepViewContainer)
        return stepViewContainer
    }

    fun getTabDrawable(position: Int): Drawable? {
        if (position >= steps?.size ?: 0) return null
        val step = steps?.get(position)
        return stepTypeResolver.getDrawableForType(step?.block?.name, step?.is_custom_passed
                ?: false, step?.actions?.doReview != null)
    }

    fun attachView() {
        presenter.attachView(this)
    }

    override fun setHeader(stepViewContainer: ViewGroup, step: Step?) {
        val header: LatexSupportableEnhancedFrameLayout = stepViewContainer.text_header
        header.setText(step?.block?.text)
        header.visibility = View.VISIBLE
    }

    override fun updateCommentState(stepViewContainer: ViewGroup, step: Step?) {
        val textForComment: TextView = stepViewContainer.open_comments_text
        when (step?.discussion_proxy?.isNotEmpty()) {
            true -> showComment(textForComment, step)
            else -> textForComment.visibility = View.GONE
        }
    }

    private fun showComment(textForComment: TextView, step: Step?) {
        textForComment.visibility = View.VISIBLE
        textForComment.setOnClickListener {
            val discussionCount = step?.discussions_count

            if (discussionCount == 0) {
                screenManager.openComments(context, step.discussion_proxy ?: "", step.id, true)
            } else {
                screenManager.openComments(context, step?.discussion_proxy ?: "", step?.id
                        ?: 0L, false)
            }
        }

        val discussionCount = step?.discussions_count ?: 0
        if (discussionCount > 0) {
            textForComment.text = App.getAppContext().resources.getQuantityString(R.plurals.open_comments, discussionCount, discussionCount)
        } else {
            textForComment.text = App.getAppContext().resources.getString(R.string.open_comments_zero)
        }
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(`object` as View)
    }

}