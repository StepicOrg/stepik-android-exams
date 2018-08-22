package org.stepik.android.exams.ui.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.recycler_item.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.graph.model.Topic
import org.stepik.android.exams.ui.activity.TopicsListActivity

class TopicsAdapter(var context: Activity, var screenManager: ScreenManager) : RecyclerView.Adapter<TopicsAdapter.TopicsViewHolder>() {
    private var topics: List<Topic> = listOf()
    private lateinit var type: TopicsListActivity.TYPE

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            TopicsViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.recycler_item, parent, false))


    override fun getItemCount() = topics.size


    override fun onBindViewHolder(holder: TopicsViewHolder?, position: Int) {
        holder?.bind(topics[position])
    }

    fun updateTopics(topics: List<Topic>) {
        this.topics = topics
        notifyDataSetChanged()
    }

    fun updateType(type: TopicsListActivity.TYPE) {
        this.type = type
    }

    inner class TopicsViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val topicsText: TextView = root.text

        init {
            topicsText.setOnClickListener {
                when (type) {
                    TopicsListActivity.TYPE.THEORY -> screenManager.showLessons(topics[adapterPosition].id, context)
                    TopicsListActivity.TYPE.ADAPTIVE -> screenManager.continueAdaptiveCourse(topics[adapterPosition].id, context)
                }
            }
        }

        fun bind(topic: Topic) {
            topicsText.text = topic.title
        }
    }
}