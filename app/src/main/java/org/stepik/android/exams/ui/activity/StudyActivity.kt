package org.stepik.android.exams.ui.activity

import android.os.Bundle
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.BasePresenterActivity
import org.stepik.android.exams.core.presenter.StudyPresenter
import org.stepik.android.exams.core.presenter.contracts.StudyView
import org.stepik.android.exams.graph.model.GraphData
import javax.inject.Inject
import javax.inject.Provider

class StudyActivity : BasePresenterActivity<StudyPresenter, StudyView>(), StudyView {
    @Inject
    lateinit var studyPresenterProvider: Provider<StudyPresenter>
    private lateinit var graph : GraphData
    override fun injectComponent() {
        App.component().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)
    }

    override fun onResume() {
        super.onResume()
        presenter?.getGraphData()
    }
    override fun loadData(graphData: GraphData) {
        graph = graphData
    }
    override fun getPresenterProvider(): Provider<StudyPresenter>  = studyPresenterProvider

    override fun onSuccess() {
    }
    override fun onStart() {
        super.onStart()
        presenter?.attachView(this)
    }

    override fun onStop() {
        presenter?.detachView(this)
        super.onStop()
    }
}