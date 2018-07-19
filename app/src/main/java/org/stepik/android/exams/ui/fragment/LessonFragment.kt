package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_study.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.BasePresenterFragment
import org.stepik.android.exams.core.presenter.LessonsPresenter
import org.stepik.android.exams.core.presenter.contracts.LessonsView
import org.stepik.android.exams.data.model.Lesson
import org.stepik.android.exams.ui.adapter.LessonsAdapter
import javax.inject.Inject
import javax.inject.Provider

class LessonFragment : BasePresenterFragment<LessonsPresenter, LessonsView>(), LessonsView {
    private lateinit var lessonsAdapter: LessonsAdapter

    @Inject
    lateinit var lessonsPresenterProvider: Provider<LessonsPresenter>

    @Inject
    lateinit var screenManager: ScreenManager

    private lateinit var id : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lessonsAdapter = LessonsAdapter(context, screenManager)
        recyclerLesson.adapter = lessonsAdapter
        recyclerLesson.layoutManager = LinearLayoutManager(context)
        id = arguments.getString("id", "")
    }

    override fun injectComponent() {
        App.component().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_study, container, false);
    }

    override fun onStart() {
        super.onStart()
        presenter?.attachView(this)
        presenter?.id = id
        presenter?.loadTheoryLessons(id)
    }
    override fun onStop() {
        presenter?.detachView(this)
        super.onStop()
    }

    companion object {
        fun newInstance(id: String): LessonFragment {
            val args = Bundle()
            args.putSerializable("id", id)
            val fragment = LessonFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getPresenterProvider() = lessonsPresenterProvider

    override fun showLessons(lesson: List<Lesson>?) {
        lessonsAdapter.addLessons(lesson)
    }
}