package org.stepik.android.exams.ui.adapter

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.header_lessons.view.*
import kotlinx.android.synthetic.main.item_lesson.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.presenter.contracts.LessonsView.Type
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

    private val inflater = LayoutInflater.from(context)

    private var lessons: List<Type> = listOf()

    override fun getItemViewType(position: Int) =
            if (position == 0) {
                VIEW_TYPE_HEADER
            } else {
                VIEW_TYPE_LESSON
            }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                VIEW_TYPE_HEADER ->
                    HeaderViewHolder(inflater.inflate(R.layout.header_lessons, parent, false))

                VIEW_TYPE_LESSON ->
                    LessonViewHolder(inflater.inflate(R.layout.item_lesson, parent, false))

                else -> throw IllegalStateException("unknown viewType = $viewType")
            }

    override fun getItemCount() = lessons.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder ->
                holder.bind(topic, lessons.size)

            is LessonViewHolder ->
                holder.bind(lessons[position - 1], position)
        }
    }

    fun setLessons(lessons: List<Type>) {
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
        private val title: TextView = root.lessonTitle
        private val index: TextView = root.lessonIndex
        private val subtitle: TextView = root.lessonSubtitle

        init {
            root.setOnClickListener {
                when (lessons[adapterPosition - 1]) {
                    is Type.Theory -> screenManager.showStepsList(topic.id, (lessons[position - 1] as Type.Theory).lessonTheoryWrapper.lesson, context)
                    else -> screenManager.continueAdaptiveCourse(topic.id, context as Activity)
                }
            }
        }

        fun bind(wrapper: Type, position: Int){
            when (wrapper) {
                is Type.Theory -> bind((lessons[position - 1] as Type.Theory).lessonTheoryWrapper.lesson, position)
                else -> bind(position - 1)
            }
        }

        private fun bind(position: Int) {
            val context = itemView.context
            index.text = context.getString(R.string.position_placeholder, position)
            title.text = context.getString(R.string.adaptive)
        }

        private fun bind(wrapper: LessonWrapper, position: Int) {
            val context = itemView.context
            index.text = context.getString(R.string.position_placeholder, position)
            title.text = wrapper.lesson.title
            subtitle.text = context.resources.getQuantityString(R.plurals.page, wrapper.lesson.steps.size, wrapper.lesson.steps.size)
        }
    }
}