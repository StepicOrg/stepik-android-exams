package org.stepik.android.exams.ui.activity

import android.support.v4.app.Fragment
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.ui.fragment.StepListFragment
import org.stepik.android.exams.util.AppConstants

class StepsActivity : SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        val lesson = intent.getParcelableExtra<LessonWrapper>(AppConstants.lesson)
        val topicId = intent.getStringExtra(AppConstants.topicId)
        return StepListFragment.newInstance(topicId, lesson)
    }

}