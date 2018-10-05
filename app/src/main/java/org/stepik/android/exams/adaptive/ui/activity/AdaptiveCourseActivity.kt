package org.stepik.android.exams.adaptive.ui.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.error_no_connection_with_button.*
import kotlinx.android.synthetic.main.fragment_recommendations.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.adaptive.core.contracts.RecommendationsView
import org.stepik.android.exams.adaptive.core.presenter.RecommendationsPresenter
import org.stepik.android.exams.adaptive.ui.adapter.QuizCardsAdapter
import org.stepik.android.exams.core.presenter.BasePresenterActivity
import org.stepik.android.exams.graph.model.Topic
import org.stepik.android.exams.util.AppConstants
import org.stepik.android.exams.util.MathUtli
import org.stepik.android.exams.util.initCenteredToolbar
import javax.inject.Inject
import javax.inject.Provider

class AdaptiveCourseActivity : BasePresenterActivity<RecommendationsPresenter, RecommendationsView>(), RecommendationsView {
    @Inject
    lateinit var recommendationsPresenterProvider: Provider<RecommendationsPresenter>

    override fun getPresenterProvider() = recommendationsPresenterProvider

    private lateinit var topic: Topic
    private val loadingPlaceholders by lazy { resources.getStringArray(R.array.recommendation_loading_placeholders) }

    override fun onCreate(savedInstanceState: Bundle?) {
        topic = intent.getParcelableExtra(AppConstants.topic) as Topic
        setContentView(R.layout.fragment_recommendations)

        initCenteredToolbar(topic.title, showHomeButton = true)

        error.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))

        tryAgain.setOnClickListener {
            presenter?.retry()
        }
        super.onCreate(savedInstanceState)
    }

    override fun injectComponent() {
        App.componentManager()
                .adaptiveComponent
                .inject(this)
    }

    override fun setState(state: RecommendationsView.State) {
        when (state) {
            RecommendationsView.State.InitPresenter ->
                presenter?.initPresenter(topic.id)
            RecommendationsView.State.Loading ->
                onLoading()
            RecommendationsView.State.RequestError ->
                onRequestError()
            RecommendationsView.State.NetworkError ->
                onConnectivityError()
            RecommendationsView.State.Success ->
                onCardLoaded()
            RecommendationsView.State.CourseCompleted ->
                onCourseCompleted()
        }
    }

    override fun onAdapter(cardsAdapter: QuizCardsAdapter) {
        cardsContainer.setAdapter(cardsAdapter)
    }

    override fun onLoading() {
        progress.visibility = View.VISIBLE
        error.visibility = View.GONE
        loadingPlaceholder.text = loadingPlaceholders[MathUtli.randomBetween(0, loadingPlaceholders.size - 1)]
    }

    override fun onCardLoaded() {
        progress.visibility = View.GONE
        cardsContainer.visibility = View.VISIBLE
    }

    private fun onError() {
        cardsContainer.visibility = View.GONE
        error.visibility = View.VISIBLE
        progress.visibility = View.GONE
    }

    override fun onConnectivityError() {
        errorMessage.setText(R.string.no_connection)
        onError()
    }

    override fun onRequestError() {
        errorMessage.setText(R.string.request_error)
        onError()
    }

    private fun onCourseState() {
        cardsContainer.visibility = View.GONE
        progress.visibility = View.GONE
        courseState.visibility = View.VISIBLE
    }

    override fun onCourseCompleted() {
        courseStateText.setText(R.string.adaptive_course_completed)
        onCourseState()
    }

    override fun onCourseNotSupported() {
        courseStateText.setText(R.string.adaptive_course_not_supported)
        onCourseState()
    }

    override fun onStart() {
        super.onStart()
        presenter?.attachView(this)
    }

    override fun onStop() {
        presenter?.detachView(this)
        super.onStop()
    }

    override fun onDestroy() {
        presenter?.destroy()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem?) =
            if (item?.itemId == android.R.id.home) {
                onBackPressed()
                true
            } else {
                super.onOptionsItemSelected(item)
            }
}