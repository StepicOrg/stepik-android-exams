package org.stepik.android.exams.ui.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.training_item_lesson.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.data.model.LessonType
import org.stepik.android.exams.ui.util.TopicColorResolver
import kotlin.properties.Delegates

class TrainingAdapter(
        private val activity: Activity,
        private val screenManager: ScreenManager
) : RecyclerView.Adapter<TrainingAdapter.LessonViewHolder>() {
    companion object {
        const val ITEM_PADDING = 60
    }

    var lessons: List<LessonType> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    private val inflater = LayoutInflater.from(activity)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LessonViewHolder =
            LessonViewHolder(inflater.inflate(R.layout.training_item_lesson, parent, false))

    private fun setItemWidth(recyclerWidth : Int): Int =
         (recyclerWidth - ITEM_PADDING) / activity.resources.getInteger(R.integer.items)

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
                        screenManager.showStepsList(lessonType.lessonTheoryWrapper.topicId, lessonType.lessonTheoryWrapper.lesson, activity)

                    is LessonType.Practice ->
                        screenManager.continueAdaptiveCourse(lessonType.lessonPracticeWrapper.topicId, activity)
                }
            }
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            itemView.layoutParams.width = setItemWidth(displayMetrics.widthPixels)
        }

        fun bind(type: LessonType) {
            val context = itemView.context
            lessonDescription.text = context.resources.getString(R.string.lesson_description)
            when (type) {
                is LessonType.Theory -> {
                    val lesson = type.lessonTheoryWrapper.lesson.lesson
                    title.text = lesson.title
                    subtitle.text = context.resources.getQuantityString(R.plurals.page, lesson.steps.size, lesson.steps.size)
                    lessonContainer.setBackgroundResource(TopicColorResolver.resolveTopicBackground(type.lessonTheoryWrapper.topicId))
                }
                is LessonType.Practice -> {
                    title.text = context.getString(R.string.lesson_item_practice_title)
                    subtitle.text = context.resources.getString(R.string.lesson_item_practice_subtitle)
                    lessonContainer.setBackgroundResource(TopicColorResolver.resolveTopicBackground(type.lessonPracticeWrapper.topicId))
                }
            }
        }
    }
}