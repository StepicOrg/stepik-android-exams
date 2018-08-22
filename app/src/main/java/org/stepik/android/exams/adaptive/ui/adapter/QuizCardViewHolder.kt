package org.stepik.android.exams.adaptive.ui.adapter

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.widget.NestedScrollView
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import kotlinx.android.synthetic.main.adaptive_quiz_card_view.view.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.adaptive.core.contracts.CardView
import org.stepik.android.exams.adaptive.core.presenter.CardPresenter
import org.stepik.android.exams.adaptive.ui.custom.CardScrollView
import org.stepik.android.exams.adaptive.ui.custom.SwipeableLayout
import org.stepik.android.exams.adaptive.ui.custom.container.ContainerView
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.ui.custom.LatexSupportableWebView
import org.stepik.android.exams.ui.steps.AttemptDelegate
import org.stepik.android.exams.util.resolvers.StepTypeResolver
import org.stepik.android.model.Step
import org.stepik.android.model.Submission
import org.stepik.android.model.adaptive.Reaction
import javax.inject.Inject

class QuizCardViewHolder(
        private val root: View
) : ContainerView.ViewHolder(root), CardView {
    private val curtain = root.curtain
    private val answersProgress = root.answersProgress
    private val titleView = root.title_adaptive
    val question: LatexSupportableWebView = root.question
    val quizViewContainer: ViewGroup = root.quizViewContainer
    val separatorAnswers: View = root.separatorAnswers

    val actionButton: Button = root.submit
    val nextButton: Button = root.next
    private val correctSign = root.correct
    private val wrongSign = root.wrong
    private val wrongButton = root.wrongRetry
    private val hint = root.hint

    val scrollContainer: CardScrollView = root.scroll
    val container: SwipeableLayout = root.container

    private val hardReaction = root.reaction_hard
    private val easyReaction = root.reaction_easy

    private lateinit var quizDelegate: AttemptDelegate

    @Inject
    lateinit var screenManager: ScreenManager

    @Inject
    lateinit var stepTypeResolver: StepTypeResolver

    init {
        App.component().inject(this)

        question.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) = onCardLoaded()
        }
        question.setOnWebViewClickListener { screenManager.openImage(root.context, it) }
        question.setLayerType(View.LAYER_TYPE_NONE, null)

        nextButton.setOnClickListener {
            container.swipeDown()
        }
        actionButton.setOnClickListener { presenter?.createSubmission() }
        wrongButton.setOnClickListener {
            presenter?.let {
                it.retrySubmission()
                quizDelegate.isEnabled = true
                resetSupplementalActions()
            }
        }
        container.setNestedScroll(scrollContainer as CardScrollView?)
    }

    private var hasSubmission = false
    private var presenter: CardPresenter? = null

    fun bind(presenter: CardPresenter) {
        this.presenter = presenter
        presenter.attachView(this)
    }

    fun onTopCard() {
        if (!hasSubmission) {
            if (presenter?.isLoading == true) {
                onSubmissionLoading()
            } else {
                actionButton.visibility = View.VISIBLE
                quizDelegate.isEnabled = true
            }
        }

        container.setSwipeListener(object : SwipeableLayout.SwipeListener() {
            override fun onScroll(scrollProgress: Float) {
                hardReaction.alpha = Math.max(2 * scrollProgress, 0f)
                easyReaction.alpha = Math.max(2 * -scrollProgress, 0f)
            }

            override fun onSwipeLeft() {
                easyReaction.alpha = 1f
                presenter?.createReaction(Reaction.NEVER_AGAIN)
            }

            override fun onSwipeRight() {
                hardReaction.alpha = 1f
                presenter?.createReaction(Reaction.MAYBE_LATER)
            }
        })
    }

    private fun onCardLoaded() {
        curtain.visibility = View.GONE
        if (presenter?.isLoading != true) answersProgress.visibility = View.GONE
    }

    override fun setStep(step: Step?) {
        quizViewContainer.removeAllViews()
        quizDelegate = stepTypeResolver.getStepDelegate(step) as AttemptDelegate

        quizViewContainer.addView(quizDelegate.createView(quizViewContainer))
        quizDelegate.actionButton = actionButton
    }

    override fun setTitle(title: String?) {
        title?.let { titleView.text = it }
    }

    override fun setQuestion(html: String?) {
        html?.let { question.setText(it) }
    }


    override fun setSubmission(submission: Submission, animate: Boolean) {
        resetSupplementalActions()
        quizDelegate.setSubmission(submission)
        when (submission.status) {
            Submission.Status.CORRECT -> {
                quizDelegate.isEnabled = false
                actionButton.visibility = View.GONE
                hasSubmission = true

                correctSign.visibility = View.VISIBLE
                nextButton.visibility = View.VISIBLE
                container.isEnabled = true

                if (submission.hint?.isNotBlank() == true) {
                    hint.text = submission.hint
                    hint.visibility = View.VISIBLE
                }

                if (animate) {
                    scrollDown()
                }
            }

            Submission.Status.WRONG -> {
                quizDelegate.isEnabled = false
                wrongSign.visibility = View.VISIBLE
                hasSubmission = true

                wrongButton.visibility = View.VISIBLE
                actionButton.visibility = View.GONE

                container.isEnabled = true
            }
        }
    }

    override fun onSubmissionConnectivityError() = onSubmissionError(R.string.no_connection)

    override fun onSubmissionRequestError() = onSubmissionError(R.string.request_error)

    private fun onSubmissionError(@StringRes errorMessage: Int) {
        if (root.parent != null) {
            Snackbar.make(root.parent as ViewGroup, errorMessage, Snackbar.LENGTH_SHORT).show()
        }
        container.isEnabled = true
        quizDelegate.isEnabled = true
        resetSupplementalActions()
    }

    override fun onSubmissionLoading() {
        resetSupplementalActions()
        container.isEnabled = false
        quizDelegate.isEnabled = false
        actionButton.visibility = View.GONE
        answersProgress.visibility = View.VISIBLE

        scrollDown()
    }

    override fun getQuizViewDelegate() = quizDelegate

    private fun scrollDown() {
        scrollContainer.post {
            scrollContainer.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun resetSupplementalActions() {
        nextButton.visibility = View.GONE
        correctSign.visibility = View.GONE
        wrongSign.visibility = View.GONE
        wrongButton.visibility = View.GONE
        answersProgress.visibility = View.GONE
        hint.visibility = View.GONE
        actionButton.visibility = View.VISIBLE
    }
}