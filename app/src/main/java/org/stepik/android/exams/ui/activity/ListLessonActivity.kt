package org.stepik.android.exams.ui.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_lessons.*
import kotlinx.android.synthetic.main.error_no_connection_with_button.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.BasePresenterActivity
import org.stepik.android.exams.core.presenter.ListLessonsPresenter
import org.stepik.android.exams.core.presenter.contracts.LessonsView
import org.stepik.android.exams.graph.model.GraphLesson
import org.stepik.android.exams.ui.adapter.ListLessonAdapter
import org.stepik.android.exams.ui.adapter.TrainingAdapter
import org.stepik.android.exams.util.AppConstants
import org.stepik.android.exams.util.changeVisibillity
import org.stepik.android.exams.util.hideAllChildren
import org.stepik.android.exams.util.initCenteredToolbar
import javax.inject.Inject
import javax.inject.Provider

class ListLessonActivity : BasePresenterActivity<ListLessonsPresenter, LessonsView>(), LessonsView {
    private lateinit var topicsLessonsAdapter: ListLessonAdapter

    @Inject
    lateinit var topicLessonsPresenterProvider: Provider<ListLessonsPresenter>

    @Inject
    lateinit var screenManager: ScreenManager

    private lateinit var type: GraphLesson.Type
    override fun injectComponent() {
        App.component().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lessons)
        type = intent.getSerializableExtra(AppConstants.TYPE_LESSONS_LIST)  as GraphLesson.Type
        when (type) {
            GraphLesson.Type.THEORY -> initCenteredToolbar(R.string.theory, showHomeButton = true)
            GraphLesson.Type.PRACTICE -> initCenteredToolbar(R.string.practice, showHomeButton = true)
        }
        topicsLessonsAdapter = ListLessonAdapter(this, screenManager)
        recyclerLesson.adapter = topicsLessonsAdapter
        recyclerLesson.layoutManager = LinearLayoutManager(this)
        swipeRefreshLessons.setOnRefreshListener {
            presenter?.loadAllTypedLessons(type)
        }

        tryAgain.setOnClickListener {
            presenter?.loadAllTypedLessons(type)
        }

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.list_divider_h))
        recyclerLesson.addItemDecoration(divider)

        content.hideAllChildren()
        loadingPlaceholder.changeVisibillity(true)

        initPlaceholders()
    }

    private fun initPlaceholders() {
        for (i in 0 until 3) {
            loadingPlaceholder.addView(layoutInflater.inflate(R.layout.item_lesson_placeholder, loadingPlaceholder, false))
            loadingPlaceholder.addView(layoutInflater.inflate(R.layout.view_divider_h, loadingPlaceholder, false))
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
        is LessonsView.State.Idle -> {
            presenter?.loadAllTypedLessons(type) ?: Unit
        }

        is LessonsView.State.Loading -> {
            content.hideAllChildren()
            loadingPlaceholder.changeVisibillity(true)
        }

        is LessonsView.State.Refreshing -> {
            content.hideAllChildren()
            swipeRefreshLessons.changeVisibillity(true)
            swipeRefreshLessons.isRefreshing = true
            topicsLessonsAdapter.lessons = state.lessons
        }

        is LessonsView.State.NetworkError -> {
            content.hideAllChildren()
            error.changeVisibillity(true)
        }

        is LessonsView.State.Success -> {
            content.hideAllChildren()
            swipeRefreshLessons.changeVisibillity(true)
            swipeRefreshLessons.isRefreshing = false
            topicsLessonsAdapter.lessons = state.lessons
        }
    }

    override fun getPresenterProvider() = topicLessonsPresenterProvider

    override fun onOptionsItemSelected(item: MenuItem?) =
            if (item?.itemId == android.R.id.home) {
                onBackPressed()
                true
            } else {
                super.onOptionsItemSelected(item)
            }
}