package org.stepik.android.exams.ui.adapter

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.header_lessons.view.*
import kotlinx.android.synthetic.main.item_topic_lesson.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.data.model.LessonType
import org.stepik.android.exams.graph.model.Topic
import org.stepik.android.exams.ui.util.TopicColorResolver
import kotlin.properties.Delegates

class TopicsLessonsAdapter(
        private val context: Context,
        private val screenManager: ScreenManager,
        private val topic: Topic
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_HEADER = 1
        private const val VIEW_TYPE_LESSON = 2
    }

    var lessons: List<LessonType> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    private val inflater = LayoutInflater.from(context)

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
                    LessonViewHolder(inflater.inflate(R.layout.item_topic_lesson, parent, false))

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

    class HeaderViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        private val topicTitle = root.topicTitle
        private val lessonsCount = root.lessonsCount

        fun bind(topic: Topic, itemCount: Int) {
            topicTitle.text = topic.title
            itemView.setBackgroundResource(TopicColorResolver.resolveTopicBackground(topic.id))
            lessonsCount.text = itemView.context.resources.getQuantityString(R.plurals.lesson, itemCount, itemCount)
        }
    }

    inner class LessonViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        private val title: TextView = root.lessonTitle
        private val index: TextView = root.lessonIndex
        private val subtitle: TextView = root.lessonSubtitle

        init {
            root.setOnClickListener {
                val lessonType = lessons[adapterPosition - 1]
                when (lessonType) {
                    is LessonType.Theory ->
                        screenManager.showStepsList(topic.id, lessonType.lessonTheoryWrapper, context)

                    is LessonType.Practice ->
                        screenManager.continueAdaptiveCourse(topic.id, context as Activity)
                }
            }
        }

        fun bind(type: LessonType, position: Int) {
            val context = itemView.context
            index.text = context.getString(R.string.position_placeholder, position)

            when (type) {
                is LessonType.Theory -> {
                    val lesson = type.lessonTheoryWrapper.lesson
                    title.text = lesson.title
                    subtitle.text = context.resources.getQuantityString(R.plurals.page, lesson.steps.size, lesson.steps.size)
                }
                is LessonType.Practice -> {
                    title.text = context.getString(R.string.lesson_item_practice_title)
                    subtitle.text = context.getString(R.string.lesson_item_practice_subtitle)
                }
            }
        }
    }
}