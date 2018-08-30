package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_training.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.TrainingPresenter
import org.stepik.android.exams.core.presenter.contracts.TrainingView
import org.stepik.android.exams.data.model.LessonType
import org.stepik.android.exams.ui.adapter.TrainingAdapter
import javax.inject.Inject

class TrainingFragment : Fragment(), TrainingView {

    companion object {
        fun newInstance(): TrainingFragment =
                TrainingFragment()
    }

    init {
        App.component().inject(this)
    }
    @Inject
    lateinit var trainingPresenter : TrainingPresenter
    @Inject
    lateinit var screenManager : ScreenManager
    private lateinit var trainingTheoryAdapter : TrainingAdapter
    private lateinit var trainingPracticeAdapter : TrainingAdapter

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trainingTheoryAdapter = TrainingAdapter(context, screenManager)
        trainingPracticeAdapter = TrainingAdapter(context, screenManager)
        theoryLessonRecycler.adapter = trainingTheoryAdapter
        theoryLessonRecycler.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        practiceLessonRecycler.adapter = trainingPracticeAdapter
        practiceLessonRecycler.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
    }

    override fun onStart() {
        super.onStart()
        trainingPresenter.attachView(this)
    }

    override fun onStop() {
        trainingPresenter.detachView(this)
        super.onStop()
    }

    override fun showPracticeLessons(practiceList: List<LessonType.Practice>) {
        trainingPracticeAdapter.setLessons(practiceList)
    }

    override fun showTheoryLessons(theoryList: List<LessonType.Theory>) {
        trainingTheoryAdapter.setLessons(theoryList)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            LayoutInflater.from(context).inflate(R.layout.fragment_training, container, false)

}