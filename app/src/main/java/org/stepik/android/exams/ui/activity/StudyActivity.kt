package org.stepik.android.exams.ui.activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.graph.Graph
import javax.inject.Inject

class StudyActivity : AppCompatActivity(){

    @Inject
    lateinit var graph: Graph<String>
    init {
        App.component().inject(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)
        var info = getCourseInfo()
    }

    private fun getCourseInfo() = graph[(intent.getStringExtra("id"))]

}