package org.stepik.android.exams.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.topics_item.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.graph.model.Topic

class TopicsAdapter : RecyclerView.Adapter<TopicsAdapter.TopicsViewHolder>() {
    private var topics: List<Topic> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TopicsViewHolder {
        return TopicsViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.topics_item, parent, false))
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    override fun onBindViewHolder(holder: TopicsViewHolder?, position: Int) {
        holder?.topicsText?.text = topics.get(position).title
    }

    fun updateTopics(topics: List<Topic>) {
        this.topics = topics
        notifyDataSetChanged()
    }

    class TopicsViewHolder(root: View): RecyclerView.ViewHolder(root) {
        val topicsText: TextView = root.topicsText
    }
}