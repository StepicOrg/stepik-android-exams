package org.stepik.android.exams.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.stepik.android.exams.App
import org.stepik.android.exams.R

class StudyActivity : AppCompatActivity() {
    init {
        App.component().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)
    }
}