package org.stepik.android.exams.ui.activity

import android.support.v4.app.Fragment
import org.stepik.android.exams.ui.fragment.LessonFragment
import org.stepik.android.exams.util.AppConstants

class LessonsActivity : SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        val lessonId = intent.getStringExtra(AppConstants.lessonId)
        return LessonFragment.newInstance(lessonId)
    }

}