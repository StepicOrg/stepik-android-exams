package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.error_no_connection_with_button.*
import kotlinx.android.synthetic.main.fragment_topics_list.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.BasePresenterFragment
import org.stepik.android.exams.core.presenter.TopicsListPresenter
import org.stepik.android.exams.core.presenter.contracts.TopicsListView
import org.stepik.android.exams.graph.model.GraphData
import org.stepik.android.exams.ui.adapter.TopicsAdapter
import org.stepik.android.exams.util.changeVisibillity
import org.stepik.android.exams.util.hideAllChildren
import org.stepik.android.exams.util.initCenteredToolbar
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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            LayoutInflater.from(context).inflate(R.layout.fragment_topics_list, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCenteredToolbar(R.string.study)

        topicsAdapter = TopicsAdapter(activity, screenManager)

        recycler.adapter = topicsAdapter
        recycler.layoutManager = LinearLayoutManager(activity)

        swipeRefresh.setOnRefreshListener {
            presenter?.getGraphData()
        }

        tryAgain.setOnClickListener {
            presenter?.getGraphData()
        }
    }

    override fun showGraphData(graphData: GraphData) {
        topicsAdapter.topics = graphData.topics
    }

    override fun getPresenterProvider(): Provider<TopicsListPresenter> =
            topicsListPresenterProvider

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

        is TopicsListView.State.Loading -> {
            content.hideAllChildren()
            loadingPlaceholder.changeVisibillity(true)
        }

        is TopicsListView.State.NetworkError -> {
            content.hideAllChildren()
            error.changeVisibillity(true)
        }

        is TopicsListView.State.Success -> {
            content.hideAllChildren()
            swipeRefresh.changeVisibillity(true)
            swipeRefresh.isRefreshing = false
            topicsAdapter.topics = state.topics
        }

        is TopicsListView.State.Refreshing -> {
            content.hideAllChildren()
            swipeRefresh.changeVisibillity(true)
            swipeRefresh.isRefreshing = true
            topicsAdapter.topics = state.topics
        }
    }
}