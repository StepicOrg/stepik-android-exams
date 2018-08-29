package org.stepik.android.exams.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.header_lessons.view.*
import kotlinx.android.synthetic.main.recycler_item.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.graph.model.Topic

class LessonsAdapter(
        private val context: Context,
        private val screenManager: ScreenManager,
        private val topic: Topic
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_HEADER = 1
        private const val VIEW_TYPE_LESSON = 2
    }

    private var lessons: List<LessonWrapper> = listOf()

    override fun getItemViewType(position: Int) =
            if (position == 0) {
                VIEW_TYPE_HEADER
            } else {
                VIEW_TYPE_LESSON
            }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder =
            when(viewType) {
                VIEW_TYPE_HEADER ->
                    HeaderViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.header_lessons, parent, false))

                VIEW_TYPE_LESSON ->
                    LessonViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.recycler_item, parent, false))

                else -> throw IllegalStateException("unknown viewType = $viewType")
            }

    override fun getItemCount() = lessons.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is HeaderViewHolder ->
                holder.bind(topic, lessons.size)

            is LessonViewHolder ->
                holder.bind(lessons[position - 1])
        }
    }

    fun addLessons(lessons: List<LessonWrapper>) {
        this.lessons = lessons
        notifyDataSetChanged()
    }

    class HeaderViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        private val topicTitle = root.topicTitle
        private val lessonsCount = root.lessonsCount

        fun bind(topic: Topic, itemCount: Int) {
            topicTitle.text = topic.title
            lessonsCount.text = itemView.context.resources.getQuantityString(R.plurals.lesson, itemCount, itemCount)
        }
    }

    inner class LessonViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        private val titleText: TextView = root.text

        init {
            titleText.setOnClickListener {
                screenManager.showStepsList(topic.id, lessons[adapterPosition - 1], context)
            }
        }

        fun bind(wrapper: LessonWrapper) {
            titleText.text = wrapper.lesson.title
        }
    }
}