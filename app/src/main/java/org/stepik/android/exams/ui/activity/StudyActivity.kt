package org.stepik.android.exams.ui.activity

import android.os.Bundle
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.BasePresenterActivity
import org.stepik.android.exams.core.presenter.StudyPresenter
import org.stepik.android.exams.core.presenter.contracts.StudyView
import javax.inject.Inject
import javax.inject.Provider

class StudyActivity : BasePresenterActivity<StudyPresenter, StudyView>(), StudyView {
    var id = ""

    @Inject
    lateinit var studyPresenterProvider: Provider<StudyPresenter>

    override fun injectComponent() {
        App.component().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)
        id = intent.getStringExtra("id")
    }

    override fun onStart() {
        super.onStart()
        presenter?.attachView(this)
        presenter?.loadTheoryLessons(id)
    }

    override fun onStop() {
        presenter?.detachView(this)
        super.onStop()
    }

    override fun getPresenterProvider() = studyPresenterProvider
}