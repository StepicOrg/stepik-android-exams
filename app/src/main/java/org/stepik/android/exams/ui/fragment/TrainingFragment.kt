package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.error_no_connection_with_button.*
import kotlinx.android.synthetic.main.fragment_training.*
import kotlinx.android.synthetic.main.view_see_all_practice.*
import kotlinx.android.synthetic.main.view_see_all_theory.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.BasePresenterFragment
import org.stepik.android.exams.core.presenter.TrainingPresenter
import org.stepik.android.exams.core.presenter.contracts.TrainingView
import org.stepik.android.exams.ui.adapter.TrainingAdapter
import org.stepik.android.exams.ui.custom.CoursesSnapHelper
import org.stepik.android.exams.ui.custom.WrappingLinearLayoutManager
import org.stepik.android.exams.util.changeVisibillity
import org.stepik.android.exams.util.hideAllChildren
import org.stepik.android.exams.util.initCenteredToolbar
import javax.inject.Inject
import javax.inject.Provider


class TrainingFragment : BasePresenterFragment<TrainingPresenter, TrainingView>(), TrainingView {

    companion object {
        fun newInstance(): TrainingFragment =
                TrainingFragment()
        const val TYPE_THEORY = "theory"
        const val TYPE_PRACTICE = "practice"
    }

    override fun injectComponent() {
        App.component().inject(this)
    }
    @Inject
    lateinit var trainingPresenterProvider: Provider<TrainingPresenter>
    @Inject
    lateinit var screenManager: ScreenManager
    private lateinit var trainingTheoryAdapter: TrainingAdapter
    private lateinit var trainingPracticeAdapter: TrainingAdapter

    override fun getPresenterProvider(): Provider<TrainingPresenter> = trainingPresenterProvider

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val theoryHelper = CoursesSnapHelper(1)
        val practiceHelper = CoursesSnapHelper(1)
        trainingTheoryAdapter = TrainingAdapter(activity, screenManager)
        trainingPracticeAdapter = TrainingAdapter(activity, screenManager)
        theoryLessonRecycler.adapter = trainingTheoryAdapter
        theoryLessonRecycler.layoutManager = WrappingLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        practiceLessonRecycler.adapter = trainingPracticeAdapter
        practiceLessonRecycler.layoutManager = WrappingLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        theoryHelper.attachToRecyclerView(theoryLessonRecycler)
        practiceHelper.attachToRecyclerView(practiceLessonRecycler)
        initCenteredToolbar(R.string.training)
        swipeRefresh.setOnRefreshListener {
            presenter?.loadTopics()
        }

        tryAgain.setOnClickListener {
            presenter?.loadTopics()
        }

        buttonSeeAllTheory.setOnClickListener {
            screenManager.showLessonsList(context, TYPE_THEORY)
        }

        buttonSeeAllPractice.setOnClickListener {
            screenManager.showLessonsList(context, TYPE_PRACTICE)
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

    override fun setState(state: TrainingView.State) = when (state) {
        is TrainingView.State.Idle -> {
        }

        is TrainingView.State.Loading -> {
            content.hideAllChildren()
            loadingPlaceholder.changeVisibillity(true)
        }

        is TrainingView.State.NetworkError -> {
            content.hideAllChildren()
            error.changeVisibillity(true)
        }

        is TrainingView.State.Success -> {
            content.hideAllChildren()
            swipeRefresh.changeVisibillity(true)
            swipeRefresh.isRefreshing = false
            trainingPracticeAdapter.lessons = state.practice
            trainingTheoryAdapter.lessons = state.theory
        }

        is TrainingView.State.Refreshing -> {
            content.hideAllChildren()
            swipeRefresh.changeVisibillity(true)
            swipeRefresh.isRefreshing = true
            trainingPracticeAdapter.lessons = state.practice
            trainingTheoryAdapter.lessons = state.theory
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            LayoutInflater.from(context).inflate(R.layout.fragment_training, container, false)

}