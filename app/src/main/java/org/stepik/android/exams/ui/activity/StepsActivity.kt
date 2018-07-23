package org.stepik.android.exams.ui.activity

import android.support.v4.app.Fragment
import org.stepik.android.exams.data.model.Lesson
import org.stepik.android.exams.ui.fragment.StepsFragment

class StepsActivity : SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        val lesson = intent.getParcelableExtra<Lesson>("lesson")
        return StepsFragment.newInstance(lesson)
    }

}