package org.stepik.android.exams.ui.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_lessons.*
import kotlinx.android.synthetic.main.error_no_connection_with_button.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.BasePresenterActivity
import org.stepik.android.exams.core.presenter.LessonsListPresenter
import org.stepik.android.exams.core.presenter.contracts.LessonsView
import org.stepik.android.exams.graph.model.GraphLesson
import org.stepik.android.exams.ui.adapter.ListLessonAdapter
import org.stepik.android.exams.util.AppConstants
import org.stepik.android.exams.util.changeVisibillity
import org.stepik.android.exams.util.hideAllChildren
import org.stepik.android.exams.util.initCenteredToolbar
import javax.inject.Inject
import javax.inject.Provider

class ListLessonActivity : BasePresenterActivity<LessonsListPresenter, LessonsView>(), LessonsView {
    private lateinit var listLessonsAdapter: ListLessonAdapter

    @Inject
    lateinit var lessonsListPresenterProvider: Provider<LessonsListPresenter>

    @Inject
    lateinit var screenManager: ScreenManager

    private lateinit var type: GraphLesson.Type
    override fun injectComponent() {
        App.component().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lessons)
        type = intent.getSerializableExtra(AppConstants.TYPE_LESSONS_LIST) as GraphLesson.Type
        val title = when (type) {
            GraphLesson.Type.THEORY -> R.string.theory
            GraphLesson.Type.PRACTICE -> R.string.practice
        }
        initCenteredToolbar(title, showHomeButton = true)
        initAdapter()

        swipeRefreshLessons.setOnRefreshListener {
            loadAllLessonsByType()
        }

        tryAgain.setOnClickListener {
            loadAllLessonsByType()
        }
        content.hideAllChildren()
        initPlaceholders()
        loadingPlaceholder.changeVisibillity(true)
    }

    private fun loadAllLessonsByType() {
        presenter?.loadAllTypedLessons(type)
    }

    private fun initAdapter(){
        listLessonsAdapter = ListLessonAdapter(this, screenManager)
        recyclerLesson.adapter = listLessonsAdapter
        recyclerLesson.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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
            loadAllLessonsByType()
        }

        is LessonsView.State.Loading -> {
            content.hideAllChildren()
            loadingPlaceholder.changeVisibillity(true)
        }

        is LessonsView.State.Refreshing -> {
            content.hideAllChildren()
            swipeRefreshLessons.changeVisibillity(true)
            swipeRefreshLessons.isRefreshing = true
            listLessonsAdapter.lessons = state.lessons
        }

        is LessonsView.State.NetworkError -> {
            content.hideAllChildren()
            error.changeVisibillity(true)
        }

        is LessonsView.State.Success -> {
            content.hideAllChildren()
            swipeRefreshLessons.changeVisibillity(true)
            swipeRefreshLessons.isRefreshing = false
            listLessonsAdapter.lessons = state.lessons
        }
    }

    override fun getPresenterProvider() = lessonsListPresenterProvider

    override fun onOptionsItemSelected(item: MenuItem?) =
            if (item?.itemId == android.R.id.home) {
                onBackPressed()
                true
            } else {
                super.onOptionsItemSelected(item)
            }
}