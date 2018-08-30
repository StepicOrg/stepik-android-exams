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
import org.stepik.android.exams.data.model.LessonType
import org.stepik.android.exams.graph.model.Topic

class TrainingAdapter(
        private val context: Context,
        private val screenManager: ScreenManager
) : RecyclerView.Adapter<TrainingAdapter.LessonViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    private var lessons: List<LessonType> = listOf()


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LessonViewHolder =
            LessonViewHolder(inflater.inflate(R.layout.item_lesson, parent, false))


    override fun getItemCount() = lessons.size

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.bind(lessons[position], position)
    }

    fun setLessons(lessons: List<LessonType>) {
        this.lessons = lessons
        notifyDataSetChanged()
    }

    inner class LessonViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        private val title: TextView = root.lessonTitle
        private val index: TextView = root.lessonIndex
        private val subtitle: TextView = root.lessonSubtitle

        init {
            root.setOnClickListener {
                val lessonType = lessons[adapterPosition]
                when (lessonType) {
                    is LessonType.Theory ->
                        screenManager.showStepsList(lessonType.lessonTheoryWrapper.theoryId, lessonType.lessonTheoryWrapper.lesson, context)

                    is LessonType.Practice ->
                        screenManager.continueAdaptiveCourse(lessonType.lessonPracticeWrapper.theoryId, context as Activity)
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
                    subtitle.text = context.resources.getQuantityString(R.plurals.page, lesson.steps.size,lesson.steps.size)
                }
                is LessonType.Practice -> {
                    title.text = context.getString(R.string.lesson_item_practice_title)
                    subtitle.text = context.resources.getString(R.string.lesson_item_practice_subtitle)
                }
            }
        }
    }
}