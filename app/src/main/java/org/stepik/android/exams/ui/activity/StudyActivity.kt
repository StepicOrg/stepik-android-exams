package org.stepik.android.exams.ui.activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.BasePresenterActivity
import org.stepik.android.exams.core.presenter.StudyPresenter
import org.stepik.android.exams.core.presenter.contracts.StudyView
import org.stepik.android.exams.graph.Graph
import javax.inject.Inject
import javax.inject.Provider

class StudyActivity : BasePresenterActivity<StudyPresenter, StudyView>(), StudyView{

    override fun injectComponent() {
        App.component().inject(this)
    }

    @Inject
    lateinit var graph: Graph<String>

    @Inject
    lateinit var studyPresenterProvider: Provider<StudyPresenter>

    init {
        App.component().inject(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)
        var info = getCourseInfo()
    }

    override fun onStart() {
        super.onStart()
        presenter?.attachView(this)
        presenter?.loadTheoryLessons()
    }

    override fun onStop() {
        presenter?.detachView(this)
        super.onStop()
    }

    private fun getCourseInfo() = graph[(intent.getStringExtra("id"))]

    override fun getPresenterProvider(): Provider<StudyPresenter> = studyPresenterProvider

}