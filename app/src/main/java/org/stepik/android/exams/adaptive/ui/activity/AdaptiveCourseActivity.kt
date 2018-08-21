package org.stepik.android.exams.ui.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.PopupWindow
import kotlinx.android.synthetic.main.error_no_connection_with_button.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.BasePresenterActivity
import org.stepik.android.exams.core.presenter.RecommendationsPresenter
import org.stepik.android.exams.core.presenter.contracts.RecommendationsView
import org.stepik.android.model.Course
import javax.inject.Inject
import javax.inject.Provider

class AdaptiveCourseActivity : BasePresenterActivity<RecommendationsPresenter, RecommendationsView>(), RecommendationsView {
    @Inject
    lateinit var recommendationsPresenterProvider: Provider<RecommendationsPresenter>

    override fun getPresenterProvider() = recommendationsPresenterProvider
    @Inject
    lateinit var recommendationsPresenter: RecommendationsPresenter

    private var course: Course? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_recommendations)
        error.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))

        tryAgain.setOnClickListener {
            recommendationsPresenter.retry()
        }

        //course = arguments.getParcelable(AppConstants.KEY_COURSE_BUNDLE)
        super.onCreate(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun injectComponent() {
        App.componentManager().adaptiveComponent.inject(this)
        /* App.componentManager()
                 .adaptiveCourseComponent(course?.courseId ?: 0)
                 .inject(this)*/
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        streakSuccessContainer.nestedTextView = streakSuccess
        streakSuccessContainer.setGradientDrawableParams(ContextCompat.getColor(context, R.color.adaptive_color_correct), 0f)
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