package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import org.stepik.android.exams.graph.model.GraphLesson
import org.stepik.android.exams.ui.adapter.LessonsAdapter
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

    private lateinit var theoryLessonsAdapter: LessonsAdapter
    private lateinit var practiceLessonsAdapter: LessonsAdapter

    override fun getPresenterProvider(): Provider<TrainingPresenter> = trainingPresenterProvider

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_training, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        theoryLessonsAdapter = LessonsAdapter(activity, screenManager)
        practiceLessonsAdapter = LessonsAdapter(activity, screenManager)

        initAdapter(theoryLessonRecycler, theoryLessonsAdapter)
        initAdapter(practiceLessonRecycler, practiceLessonsAdapter)

        initSnapHelper(theoryLessonRecycler)
        initSnapHelper(practiceLessonRecycler)

        initCenteredToolbar(R.string.training)
        swipeRefresh.setOnRefreshListener {
            presenter?.loadTopics()
        }

        tryAgain.setOnClickListener {
            presenter?.loadTopics()
        }

        buttonSeeAllTheory.setOnClickListener {
            screenManager.showLessonsList(context, GraphLesson.Type.THEORY)
        }

        buttonSeeAllPractice.setOnClickListener {
            screenManager.showLessonsList(context, GraphLesson.Type.PRACTICE)
        }
    }

    private fun initAdapter(recyclerView: RecyclerView, adapter: LessonsAdapter) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = WrappingLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false, context.resources.getInteger(R.integer.training_columns))
    }

    private fun initSnapHelper(recyclerView : RecyclerView){
        val snapHelper = CoursesSnapHelper(1)
        snapHelper.attachToRecyclerView(recyclerView)
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
            practiceLessonsAdapter.lessons = state.practice
            theoryLessonsAdapter.lessons = state.theory
        }

        is TrainingView.State.Refreshing -> {
            content.hideAllChildren()
            swipeRefresh.changeVisibillity(true)
            swipeRefresh.isRefreshing = true
            practiceLessonsAdapter.lessons = state.practice
            theoryLessonsAdapter.lessons = state.theory
        }
    }
}