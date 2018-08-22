package org.stepik.android.exams.adaptive.ui.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.error_no_connection_with_button.*
import kotlinx.android.synthetic.main.fragment_recommendations.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.adaptive.core.contracts.RecommendationsView
import org.stepik.android.exams.adaptive.core.presenter.RecommendationsPresenter
import org.stepik.android.exams.adaptive.ui.adapter.QuizCardsAdapter
import org.stepik.android.exams.util.AppConstants
import javax.inject.Inject

class AdaptiveCourseActivity : AppCompatActivity(), RecommendationsView {
    @Inject
    lateinit var recommendationsPresenter: RecommendationsPresenter

    private var course: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        course = intent.getLongExtra(AppConstants.topicId, 8290)
        inject()
        setContentView(R.layout.fragment_recommendations)
        error.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))

        tryAgain.setOnClickListener {
            recommendationsPresenter.retry()
        }
        super.onCreate(savedInstanceState)
    }

    private fun inject() {
        App.component().adaptiveComponentBuilder().courseId(course).build()
                .inject(this)
    }

    override fun onAdapter(cardsAdapter: QuizCardsAdapter) {
        cardsContainer.setAdapter(cardsAdapter)
    }

    override fun onLoading() {
        progress.visibility = View.VISIBLE
        error.visibility = View.GONE
        //loadingPlaceholder.text = loadingPlaceholders[MathUtli.randomBetween(0, loadingPlaceholders.size - 1)]
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
        recommendationsPresenter.attachView(this)
    }

    override fun onStop() {
        recommendationsPresenter.detachView(this)
        super.onStop()
    }


    override fun onDestroy() {
        recommendationsPresenter.destroy()
        super.onDestroy()
    }
}