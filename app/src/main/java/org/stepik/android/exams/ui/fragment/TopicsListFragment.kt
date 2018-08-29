package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_topics_list.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.api.Errors
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.BasePresenterFragment
import org.stepik.android.exams.core.presenter.TopicsListPresenter
import org.stepik.android.exams.core.presenter.contracts.TopicsListView
import org.stepik.android.exams.graph.model.GraphData
import org.stepik.android.exams.ui.adapter.TopicsAdapter
import org.stepik.android.exams.util.changeVisibillity
import javax.inject.Inject
import javax.inject.Provider

class TopicsListFragment : BasePresenterFragment<TopicsListPresenter, TopicsListView>(), TopicsListView {
    companion object {
        fun newInstance(): TopicsListFragment =
                TopicsListFragment()
    }

    @Inject
    lateinit var topicsListPresenterProvider: Provider<TopicsListPresenter>
    @Inject
    lateinit var screenManager: ScreenManager
    private lateinit var topicsAdapter: TopicsAdapter

    override fun injectComponent() {
        App.component().inject(this)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topicsAdapter = TopicsAdapter(activity, screenManager)
        recycler.adapter = topicsAdapter
        recycler.layoutManager = LinearLayoutManager(activity)
        swipeRefresh.setOnRefreshListener {
            presenter?.getGraphData()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            LayoutInflater.from(context).inflate(R.layout.fragment_topics_list, container, false)

    override fun showGraphData(graphData: GraphData) {
        topicsAdapter.updateData(graphData.topics)
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

        is TopicsListView.State.NetworkError -> {
            hideRefreshView()
            onError(Errors.ConnectionProblem)
        }

        is TopicsListView.State.Success -> {
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
        swipeRefresh.isRefreshing = true
    }

    private fun hideRefreshView() {
        swipeRefresh.isRefreshing = false
    }

    private fun showErrorMessage(messageResId: Int) {
        errorText.setText(messageResId)
        errorText.changeVisibillity(true)
    }

    private fun hideErrorMessage() {
        errorText.changeVisibillity(false)
    }
}