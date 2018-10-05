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
import org.stepik.android.exams.core.presenter.TopicLessonsPresenter
import org.stepik.android.exams.core.presenter.contracts.LessonsView
import org.stepik.android.exams.graph.model.Topic
import org.stepik.android.exams.ui.adapter.TopicsLessonsAdapter
import org.stepik.android.exams.util.changeVisibillity
import org.stepik.android.exams.util.hideAllChildren
import org.stepik.android.exams.util.initCenteredToolbar
import javax.inject.Inject
import javax.inject.Provider

class TopicLessonsActivity : BasePresenterActivity<TopicLessonsPresenter, LessonsView>(), LessonsView {
    companion object {
        const val EXTRA_TOPIC = "topic"
    }

    private lateinit var topicsLessonsAdapter: TopicsLessonsAdapter

    @Inject
    lateinit var topicLessonsPresenterProvider: Provider<TopicLessonsPresenter>

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

        topicsLessonsAdapter = TopicsLessonsAdapter(this, screenManager, topic)

        recyclerLesson.adapter = topicsLessonsAdapter
        recyclerLesson.layoutManager = LinearLayoutManager(this)
        swipeRefreshLessons.setOnRefreshListener {
            loadTopicLessons()
        }

        tryAgain.setOnClickListener {
            loadTopicLessons()
        }

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.list_divider_h))
        recyclerLesson.addItemDecoration(divider)

        content.hideAllChildren()
        loadingPlaceholder.changeVisibillity(true)

        initPlaceholders()
    }

    private fun loadTopicLessons() {
        presenter?.loadTopicsLessons(topic)
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
            loadTopicLessons()
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