package org.stepik.android.exams.ui.adapter.lessons

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.recycler_item.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.graph.model.Topic

class LessonsAdapter(
        private val context: Context,
        private val screenManager: ScreenManager,
        private val topic: Topic
) : RecyclerView.Adapter<LessonsAdapter.StudyViewHolder>() {
    private var lessons: List<LessonWrapper> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            StudyViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.recycler_item, parent, false))

    override fun getItemCount() = lessons.size

    override fun onBindViewHolder(holder: StudyViewHolder?, position: Int) {
        holder?.bind(lessons[position])
    }

    fun addLessons(lessons: List<LessonWrapper>) {
        this.lessons = lessons
        notifyDataSetChanged()
    }

    inner class StudyViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        private val titleText: TextView = root.text

        init {
            titleText.setOnClickListener {
                screenManager.showStepsList(topic.id, lessons[adapterPosition], context)
            }
        }

        fun bind(wrapper: LessonWrapper) {
            titleText.text = wrapper.lesson.title
        }
    }
}