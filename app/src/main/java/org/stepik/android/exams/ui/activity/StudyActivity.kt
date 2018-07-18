package org.stepik.android.exams.ui.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_study.*
import kotlinx.android.synthetic.main.activity_topics_list.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.BasePresenterActivity
import org.stepik.android.exams.core.presenter.StudyPresenter
import org.stepik.android.exams.core.presenter.contracts.StudyView
import org.stepik.android.exams.data.model.Lesson
import org.stepik.android.exams.ui.adapter.StudyAdapter
import javax.inject.Inject
import javax.inject.Provider

class StudyActivity : BasePresenterActivity<StudyPresenter, StudyView>(), StudyView {
    var id = ""

    private lateinit var studyAdapter: StudyAdapter

    @Inject
    lateinit var studyPresenterProvider: Provider<StudyPresenter>

    @Inject
    lateinit var screenManager: ScreenManager

    override fun injectComponent() {
        App.component().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)
        id = intent.getStringExtra("id")
        studyAdapter = StudyAdapter(this, screenManager)
        recyclerLesson.adapter = studyAdapter
        recyclerLesson.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        presenter?.attachView(this)
        presenter?.loadTheoryLessons(id)
    }

    override fun showLessons(lesson: List<Lesson>?) {
        studyAdapter.addLessons(lesson)
    }

    override fun onStop() {
        presenter?.detachView(this)
        super.onStop()
    }

    override fun getPresenterProvider() = studyPresenterProvider
}