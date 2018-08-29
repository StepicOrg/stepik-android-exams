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
import org.stepik.android.exams.graph.model.Topic
import org.stepik.android.exams.ui.activity.TopicsListActivity

class TopicsAdapter(var context: Activity, var screenManager: ScreenManager) : RecyclerView.Adapter<TopicsAdapter.TopicsViewHolder>() {
    private var topics: List<Topic> = listOf()
    private lateinit var type: TopicsListActivity.TYPE

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            TopicsViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_lesson, parent, false))


    override fun getItemCount() = topics.size


    override fun onBindViewHolder(holder: TopicsViewHolder?, position: Int) {
        holder?.bind(topics[position])
    }

    fun updateData(topics: List<Topic>) {
        this.topics = topics
        notifyDataSetChanged()
    }

    fun updateType(type: TopicsListActivity.TYPE) {
        this.type = type
    }

    inner class TopicsViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        private val topicsText: TextView = root.lessonTitle

        init {
            topicsText.setOnClickListener {
                if (adapterPosition !in topics.indices) return@setOnClickListener

                when (type) {
                    TopicsListActivity.TYPE.THEORY -> screenManager.showLessons(context, topics[adapterPosition])
                    TopicsListActivity.TYPE.ADAPTIVE -> screenManager.continueAdaptiveCourse(topics[adapterPosition].id, context)
                }
            }
        }

        fun bind(topic: Topic) {
            topicsText.text = topic.title
        }
    }
}