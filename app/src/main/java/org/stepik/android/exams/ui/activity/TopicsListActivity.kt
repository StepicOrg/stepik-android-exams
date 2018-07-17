package org.stepik.android.exams.ui.activity

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_topics_list.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.api.Errors
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.BasePresenterActivity
import org.stepik.android.exams.core.presenter.TopicsListPresenter
import org.stepik.android.exams.core.presenter.contracts.TopicsListView
import org.stepik.android.exams.graph.model.GraphData
import org.stepik.android.exams.ui.adapter.TopicsAdapter
import org.stepik.android.exams.util.changeVisibillity
import javax.inject.Inject
import javax.inject.Provider

class TopicsListActivity : BasePresenterActivity<TopicsListPresenter, TopicsListView>(), TopicsListView {
    @Inject
    lateinit var topicsListPresenterProvider: Provider<TopicsListPresenter>

    @Inject
    lateinit var screenManager: ScreenManager

    private lateinit var topicsAdapter: TopicsAdapter

    override fun injectComponent() {
        App.component().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics_list)
        topicsAdapter = TopicsAdapter(this, screenManager)
        recycler.adapter = topicsAdapter
        recycler.layoutManager = LinearLayoutManager(this)
        swipeRefresh.setOnRefreshListener {
            presenter?.getGraphData()
        }
    }

    override fun showGraphData(graphData: GraphData) {
        topicsAdapter.updateTopics(graphData.topics)
    }

    override fun getPresenterProvider(): Provider<TopicsListPresenter> = topicsListPresenterProvider

    override fun onStart() {
        super.onStart()
        presenter?.attachView(this)
    }

    override fun onStop() {
        presenter?.detachView(this)
        super.onStop()
    }

    override fun setState(state: TopicsListView.State) = when (state) {
        is TopicsListView.State.Idle -> {
        }

        is TopicsListView.State.Loading ->
            showRefreshView()

        is TopicsListView.State.NetworkError ->
            onError(Errors.ConnectionProblem)

        is TopicsListView.State.Success ->
            hideRefreshView()
    }

    override fun onError(error: Errors) {
        @StringRes val messageResId = when (error) {
            Errors.ConnectionProblem -> R.string.auth_error_connectivity
        }
        errorText.setText(messageResId)
        errorText.changeVisibillity(true)
    }

    override fun showRefreshView() {
        swipeRefresh.isRefreshing = true
    }

    override fun hideRefreshView() {
        swipeRefresh.isRefreshing = false
    }
}