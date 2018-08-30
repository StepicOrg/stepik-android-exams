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

class TopicsAdapter(var context: Activity, var screenManager: ScreenManager) : RecyclerView.Adapter<TopicsAdapter.TopicsViewHolder>() {
    private var topics: List<Topic> = listOf()

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

    inner class TopicsViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        private val topicsText: TextView = root.lessonTitle

        init {
            topicsText.setOnClickListener {
                if (adapterPosition !in topics.indices) return@setOnClickListener
                screenManager.showLessons(context, topics[adapterPosition])
            }
        }

        fun bind(topic: Topic) {
            topicsText.text = topic.title
        }
    }
}