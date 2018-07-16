package org.stepik.android.exams.ui.adapter

import android.app.Activity
import android.content.Context
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

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = TopicsViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.topics_item, parent, false))


    override fun getItemCount() = topics.size


    override fun onBindViewHolder(holder: TopicsViewHolder?, position: Int) {
        holder?.bindItems(topics[position], screenManager, context)
    }

    fun updateTopics(topics: List<Topic>) {
        this.topics = topics
        notifyDataSetChanged()
    }

    class TopicsViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val topicsText: TextView = root.topicsText

        fun bindItems(topic: Topic, screenManager: ScreenManager, context: Activity){
            topicsText.text = topic.title
            topicsText.setOnClickListener{
                screenManager.showCourse(topic.id, context)
            }
        }
    }
}