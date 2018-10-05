package org.stepik.android.exams.ui.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_lesson.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.data.model.LessonType
import org.stepik.android.exams.ui.util.TopicColorResolver
import kotlin.properties.Delegates

class LessonsAdapter(
        private val activity: Activity,
        private val screenManager: ScreenManager
) : RecyclerView.Adapter<LessonsAdapter.LessonViewHolder>() {
    var lessons: List<LessonType> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    private val inflater = LayoutInflater.from(activity)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LessonViewHolder =
            LessonViewHolder(inflater.inflate(R.layout.item_lesson, parent, false))

    override fun getItemCount() = lessons.size

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.bind(lessons[position])
    }

    inner class LessonViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        private val title: TextView = root.lessonTitle
        private val subtitle: TextView = root.lessonSubtitle
        private val lessonContainer: View = root.lessonContainer
        private val lessonDescription: TextView = root.lessonDescription
        init {
            root.setOnClickListener {
                val lessonType = lessons[adapterPosition]
                when (lessonType) {
                    is LessonType.Theory ->
                        screenManager.showStepsList(lessonType.lessonTheoryWrapper.topicId, lessonType.lessonTheoryWrapper, activity)

                    is LessonType.Practice ->
                        screenManager.continueAdaptiveCourse(lessonType.lessonPracticeWrapper.topic, activity)
                }
            }
        }

        fun bind(type: LessonType) {
            val context = itemView.context
            lessonDescription.text = context.resources.getString(R.string.lesson_description)
            when (type) {
                is LessonType.Theory -> {
                    val lesson = type.lessonTheoryWrapper.lesson
                    title.text = lesson.title
                    subtitle.text = context.resources.getQuantityString(R.plurals.page, lesson.steps.size, lesson.steps.size)
                    lessonContainer.setBackgroundResource(TopicColorResolver.resolveTopicBackground(type.lessonTheoryWrapper.topicId))
                }
                is LessonType.Practice -> {
                    title.text = context.getString(R.string.lesson_item_practice_title)
                    subtitle.text = context.resources.getString(R.string.lesson_item_practice_subtitle)
                    lessonContainer.setBackgroundResource(TopicColorResolver.resolveTopicBackground(type.lessonPracticeWrapper.topic.id))
                }
            }
        }
    }
}