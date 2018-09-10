package org.stepik.android.exams.ui.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_list_lesson.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.data.model.LessonType
import org.stepik.android.exams.graph.model.Topic
import org.stepik.android.exams.ui.util.TopicColorResolver

class ListLessonsAdapter(
        private val activity: Activity,
        private val screenManager: ScreenManager,
        private val topic: Topic
) : RecyclerView.Adapter<ListLessonsAdapter.LessonViewHolder>() {

    private val inflater = LayoutInflater.from(activity)

    private var lessons: List<LessonType> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LessonViewHolder =
            LessonViewHolder(inflater.inflate(R.layout.item_list_lesson, parent, false))

    override fun getItemCount() = lessons.size + 1

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.bind(lessons[position - 1], position)
    }

    //TODO replace
    fun setLessons(lessons: List<LessonType>) {
        this.lessons = lessons
        notifyDataSetChanged()
    }

    inner class LessonViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        private val title: TextView = root.lessonTitle
        private val index: TextView = root.lessonIndex
        private val topicTitle: TextView = root.topicTitle
        private val subtitle: TextView = root.lessonSubtitle


        init {
            root.setOnClickListener {
                val lessonType = lessons[adapterPosition - 1]
                when (lessonType) {
                    is LessonType.Theory ->
                        screenManager.showStepsList(topic.id, lessonType.lessonTheoryWrapper.lesson, activity)

                    is LessonType.Practice ->
                        screenManager.continueAdaptiveCourse(topic.id, activity)
                }
            }
        }

        fun bind(type: LessonType, position: Int) {
            val context = itemView.context
            index.text = context.getString(R.string.position_placeholder, position)
            when (type) {
                is LessonType.Theory -> {
                    val lesson = type.lessonTheoryWrapper.lesson.lesson
                    title.text = lesson.title
                    subtitle.text = context.resources.getQuantityString(R.plurals.page, lesson.steps.size, lesson.steps.size)
                    val topicId = type.lessonTheoryWrapper.topicId
                    topicTitle.text = topicId
                    topicTitle.setBackgroundResource(TopicColorResolver.resolveTopicBackground(topicId))
                }
                is LessonType.Practice -> {
                    title.text = context.getString(R.string.lesson_item_practice_title)
                    subtitle.text = context.getString(R.string.lesson_item_practice_subtitle)
                    val topicId = type.lessonPracticeWrapper.topicId
                    topicTitle.text = topicId
                    topicTitle.setBackgroundResource(TopicColorResolver.resolveTopicBackground(topicId))
                }
            }
        }
    }
}