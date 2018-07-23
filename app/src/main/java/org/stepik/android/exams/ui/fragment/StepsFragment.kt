package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_steps.*
import org.stepik.android.exams.R
import org.stepik.android.exams.data.model.Lesson
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.ui.adapter.StepFragmentAdapter
import org.stepik.android.exams.util.resolvers.StepTypeImpl
import org.stepik.android.exams.util.resolvers.StepTypeResolver
import javax.inject.Inject


class StepsFragment : Fragment() {

    lateinit var stepTypeResolver: StepTypeResolver

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stepTypeResolver = StepTypeImpl(context)
        val lesson: Lesson = arguments.getParcelable("lesson")
        val mPagerAdapter: PagerAdapter = StepFragmentAdapter(context, lesson.stepsList as List<Step?>, stepTypeResolver)
        pager.adapter = mPagerAdapter
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater?.inflate(R.layout.fragment_steps, container, false)

    companion object {
        fun newInstance(lesson: Lesson): StepsFragment {
            val args = Bundle()
            args.putParcelable("lesson", lesson)
            val fragment = StepsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}