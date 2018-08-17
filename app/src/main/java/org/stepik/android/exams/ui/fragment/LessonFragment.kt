package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_study.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.api.Errors
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.BasePresenterFragment
import org.stepik.android.exams.core.presenter.LessonsPresenter
import org.stepik.android.exams.core.presenter.contracts.LessonsView
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.ui.adapter.LessonsAdapter
import org.stepik.android.exams.util.AppConstants
import org.stepik.android.exams.util.changeVisibillity
import javax.inject.Inject
import javax.inject.Provider

class LessonFragment : BasePresenterFragment<LessonsPresenter, LessonsView>(), LessonsView {
    private lateinit var lessonsAdapter: LessonsAdapter

    @Inject
    lateinit var lessonsPresenterProvider: Provider<LessonsPresenter>

    @Inject
    lateinit var screenManager: ScreenManager

    private lateinit var topicId: String

    companion object {
        fun newInstance(topicId: String): LessonFragment {
            val args = Bundle()
            args.putString(AppConstants.topicId, topicId)
            val fragment = LessonFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun injectComponent() {
        App.component().inject(this)
    }

    override fun setState(state: LessonsView.State): Unit = when (state) {
        is LessonsView.State.FirstLoading -> {
            presenter?.tryJoinCourse(topicId) ?: Unit
            presenter?.tryLoadLessons(topicId) ?: Unit
        }


        is LessonsView.State.Idle -> {
        }

        is LessonsView.State.Loading -> {
            showRefreshView()
        }

        is LessonsView.State.NetworkError -> {
            hideRefreshView()
            onError(Errors.ConnectionProblem)
        }

        is LessonsView.State.Success -> {
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
        swipeRefreshLessons.isRefreshing = true
    }

    private fun hideRefreshView() {
        swipeRefreshLessons.isRefreshing = false
    }

    private fun showErrorMessage(messageResId: Int) {
        errorTextLesson.setText(messageResId)
        errorTextLesson.changeVisibillity(true)
    }

    private fun hideErrorMessage() {
        errorTextLesson.changeVisibillity(false)
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater?.inflate(R.layout.fragment_study, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topicId = arguments.getString(AppConstants.topicId, "")
        lessonsAdapter = LessonsAdapter(context, screenManager, topicId)
        recyclerLesson.adapter = lessonsAdapter
        recyclerLesson.layoutManager = LinearLayoutManager(context)
        swipeRefreshLessons.setOnRefreshListener {
            presenter?.tryLoadLessons(topicId)
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

    override fun getPresenterProvider() = lessonsPresenterProvider

    override fun showLessons(lesson: List<LessonWrapper>) {
        lessonsAdapter.addLessons(lesson)
    }
}