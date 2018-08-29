package org.stepik.android.exams.ui.activity

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_lessons.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.api.Errors
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.BasePresenterActivity
import org.stepik.android.exams.core.presenter.LessonsPresenter
import org.stepik.android.exams.core.presenter.contracts.LessonsView
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.graph.model.Topic
import org.stepik.android.exams.ui.adapter.LessonsAdapter
import org.stepik.android.exams.util.changeVisibillity
import org.stepik.android.exams.util.initCenteredToolbar
import javax.inject.Inject
import javax.inject.Provider

class LessonsActivity : BasePresenterActivity<LessonsPresenter, LessonsView>(), LessonsView {
    companion object {
        const val EXTRA_TOPIC = "topic"
    }

    private lateinit var lessonsAdapter: LessonsAdapter

    @Inject
    lateinit var lessonsPresenterProvider: Provider<LessonsPresenter>

    @Inject
    lateinit var screenManager: ScreenManager

    private lateinit var topic: Topic

    override fun injectComponent() {
        App.component().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topic = intent.getParcelableExtra(EXTRA_TOPIC)
        setContentView(R.layout.activity_lessons)

        initCenteredToolbar(R.string.topic, showHomeButton = true)

        lessonsAdapter = LessonsAdapter(this, screenManager, topic)

        recyclerLesson.adapter = lessonsAdapter
        recyclerLesson.layoutManager = LinearLayoutManager(this)
        swipeRefreshLessons.setOnRefreshListener {
            presenter?.tryLoadLessons(topic.id)
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

    override fun setState(state: LessonsView.State): Unit = when (state) {
        is LessonsView.State.FirstLoading -> {
            presenter?.tryLoadLessons(topic.id) ?: Unit
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

    override fun getPresenterProvider() = lessonsPresenterProvider

    override fun showLessons(lesson: List<LessonWrapper>) {
        lessonsAdapter.addLessons(lesson)
    }

    override fun onOptionsItemSelected(item: MenuItem?) =
            if (item?.itemId == android.R.id.home) {
                onBackPressed()
                true
            } else {
                super.onOptionsItemSelected(item)
            }
}