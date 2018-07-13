package org.stepik.android.exams.ui.activity
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.stepik.android.exams.R
import org.stepik.android.exams.graph.Graph
import javax.inject.Inject

class StudyActivity : AppCompatActivity(){
    @Inject
    lateinit var graph: Graph<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)
    }

    private fun getCourseInfo(){
        var topic = graph.getVertex(intent.getStringExtra("id"))
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if ((requestCode == ListActivity.REQUEST_CODE) && resultCode == Activity.RESULT_OK) {
            finish()
        }
    }
}