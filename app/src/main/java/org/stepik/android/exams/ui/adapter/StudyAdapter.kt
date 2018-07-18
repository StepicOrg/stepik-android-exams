package org.stepik.android.exams.ui.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.recycler_item.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.data.model.Lesson

class StudyAdapter(var context: Activity, var screenManager: ScreenManager) : RecyclerView.Adapter<StudyAdapter.StudyViewHolder>() {
    private var lessons: List<Lesson>? = listOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            StudyViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.recycler_item, parent, false))


    override fun getItemCount() = lessons!!.size


    override fun onBindViewHolder(holder: StudyViewHolder?, position: Int) {
        holder?.bind(lessons?.get(position))
    }

    fun addLessons(lessons: List<Lesson>?) {
        this.lessons = lessons
        notifyDataSetChanged()
    }

    inner class StudyViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val studyText: TextView = root.text

        init {
            studyText.setOnClickListener {

            }
        }

        fun bind(lesson: Lesson?) {
            studyText.text = lesson?.title
        }
    }
}