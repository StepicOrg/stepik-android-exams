package org.stepik.android.exams.ui.activity

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_study.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.api.Errors
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.BasePresenterActivity
import org.stepik.android.exams.core.presenter.LessonsPresenter
import org.stepik.android.exams.core.presenter.contracts.LessonsView
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.ui.adapter.LessonsAdapter
import org.stepik.android.exams.util.AppConstants
import org.stepik.android.exams.util.changeVisibillity
import javax.inject.Inject
import javax.inject.Provider

class LessonsActivity : BasePresenterActivity<LessonsPresenter, LessonsView>(), LessonsView {
    companion object {
        const val EXTRA_TOPIC_ID = "topicId"
    }
    private lateinit var lessonsAdapter: LessonsAdapter

    @Inject
    lateinit var lessonsPresenterProvider: Provider<LessonsPresenter>

    @Inject
    lateinit var screenManager: ScreenManager

    private lateinit var topicId: String

    override fun injectComponent() {
        App.component().inject(this)
    }

    override fun setState(state: LessonsView.State): Unit = when (state) {
        is LessonsView.State.FirstLoading -> {
            presenter?.tryLoadLessons(topicId) ?: Unit
        }


        is LessonsView.State.Idle -> {
        }

        is LessonsView.State.Loading -> {
            showRefreshView()
        }

        is LessonsView.State.NetworkError -> {
            hideRefreshView()
            onError(Errors.ConnectionProblem)
        }

        is LessonsView.State.Success -> {
            hideRefreshView()
            hideErrorMessage()
        }
    }

    private fun onError(error: Errors) {
        @StringRes val messageResId = when (error) {
            Errors.ConnectionProblem -> R.string.auth_error_connectivity
        }
        showErrorMessage(messageResId)
    }

    private fun showRefreshView() {
        swipeRefreshLessons.isRefreshing = true
    }

    private fun hideRefreshView() {
        swipeRefreshLessons.isRefreshing = false
    }

    private fun showErrorMessage(messageResId: Int) {
        errorTextLesson.setText(messageResId)
        errorTextLesson.changeVisibillity(true)
    }

    private fun hideErrorMessage() {
        errorTextLesson.changeVisibillity(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topicId = intent.getStringExtra(EXTRA_TOPIC_ID)
        setContentView(R.layout.fragment_study)
        lessonsAdapter = LessonsAdapter(this, screenManager, topicId)
        recyclerLesson.adapter = lessonsAdapter
        recyclerLesson.layoutManager = LinearLayoutManager(this)
        swipeRefreshLessons.setOnRefreshListener {
            presenter?.tryLoadLessons(topicId)
        }

    }

    override fun onStart() {
        super.onStart()
        presenter?.attachView(this)
    }

    override fun onStop() {
        presenter?.detachView(this)
        super.onStop()
    }

    override fun getPresenterProvider() = lessonsPresenterProvider

    override fun showLessons(lesson: List<LessonWrapper>) {
        lessonsAdapter.addLessons(lesson)
    }
}