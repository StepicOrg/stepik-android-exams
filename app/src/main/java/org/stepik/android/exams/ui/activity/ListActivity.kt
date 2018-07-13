package org.stepik.android.exams.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.BasePresenterActivity
import org.stepik.android.exams.core.presenter.ListPresenter
import org.stepik.android.exams.core.presenter.contracts.ListView
import org.stepik.android.exams.graph.model.GraphData
import org.stepik.android.exams.ui.adapter.TopicsAdapter
import javax.inject.Inject
import javax.inject.Provider

class ListActivity : BasePresenterActivity<ListPresenter, ListView>(), ListView {
    companion object{
    const val REQUEST_CODE = 344
}
    @Inject
    lateinit var listPresenterProvider: Provider<ListPresenter>

    @Inject
    lateinit var screenManager: ScreenManager

    private lateinit var topicsAdapter: TopicsAdapter

    override fun injectComponent() {
        App.component().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        topicsAdapter = TopicsAdapter(this, screenManager)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = topicsAdapter
    }

    override fun loadData(graphData: GraphData) {
        topicsAdapter.updateTopics(graphData.topics)
    }
    override fun getPresenterProvider(): Provider<ListPresenter>  = listPresenterProvider
    
    override fun onStart() {
        super.onStart()
        presenter?.attachView(this)
        presenter?.getGraphData()
    }

    override fun onStop() {
        presenter?.detachView(this)
        super.onStop()
    }
}