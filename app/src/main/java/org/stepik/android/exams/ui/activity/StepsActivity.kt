package org.stepik.android.exams.ui.activity

import android.support.v4.app.Fragment
import org.stepik.android.exams.data.model.Lesson
import org.stepik.android.exams.ui.fragment.StepListFragment

class StepsActivity : SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        val lesson = intent.getParcelableExtra<Lesson>("lesson")
        val id = intent.getStringExtra("id")
        return StepListFragment.newInstance(id, lesson)
    }

}