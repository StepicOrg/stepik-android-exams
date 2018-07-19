package org.stepik.android.exams.ui.activity

import android.support.v4.app.Fragment
import org.stepik.android.exams.ui.fragment.LessonFragment

class LessonsActivity : SingleFragmentActivity(){

    override fun createFragment(): Fragment {
        val id = intent.getStringExtra("id")
        return LessonFragment.newInstance(id)
    }

}