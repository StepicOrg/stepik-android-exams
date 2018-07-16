package org.stepik.android.exams.ui.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.topics_item.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.graph.model.Topic

class TopicsAdapter(var context : Activity, var screenManager: ScreenManager) : RecyclerView.Adapter<TopicsAdapter.TopicsViewHolder>() {
    private var topics: List<Topic> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            TopicsViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.topics_item, parent, false), topics, screenManager, context)


    override fun getItemCount() = topics.size


    override fun onBindViewHolder(holder: TopicsViewHolder?, position: Int) {
        holder?.bind(topics[position])
    }

    fun updateTopics(topics: List<Topic>) {
        this.topics = topics
        notifyDataSetChanged()
    }

    class TopicsViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val topicsText: TextView = root.topicsText
        constructor(root: View, topic: List<Topic>, screenManager: ScreenManager, context: Activity) : this(root) {
            topicsText.setOnClickListener{
                screenManager.showCourse(topic[adapterPosition].id, context)
            }
        }
        fun bind(topic: Topic){
            topicsText.text = topic.title
        }
    }
}