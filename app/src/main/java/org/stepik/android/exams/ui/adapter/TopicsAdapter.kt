package org.stepik.android.exams.ui.adapter

import android.app.Activity
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_topic.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.graph.model.Topic
import org.stepik.android.exams.ui.util.TopicColorResolver
import kotlin.properties.Delegates

class TopicsAdapter(
        private val context: Activity,
        private val screenManager: ScreenManager
) : RecyclerView.Adapter<TopicsAdapter.TopicsViewHolder>() {
    var topics: List<Topic> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            TopicsViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_topic, parent, false))

    override fun getItemCount() = topics.size

    override fun onBindViewHolder(holder: TopicsViewHolder?, position: Int) {
        holder?.bind(topics[position])
    }

    inner class TopicsViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        private val topicTitle: TextView = root.topicTitle
        private val topicContainer: View = root.topicContainer
        private val topicTimeToComplete: TextView = root.topicTimeToComplete
        private val topicCompletionRate: TextView = root.topicCompletionRate

        init {
            topicContainer.setOnClickListener {
                if (adapterPosition !in topics.indices) return@setOnClickListener
                screenManager.showLessons(context, topics[adapterPosition])
            }

            val timeIcon = AppCompatResources.getDrawable(root.context, R.drawable.ic_time)
            topicTimeToComplete.setCompoundDrawablesWithIntrinsicBounds(timeIcon, null, null, null)

            val completionIcon = AppCompatResources.getDrawable(root.context, R.drawable.ic_completion)
            topicCompletionRate.setCompoundDrawablesWithIntrinsicBounds(completionIcon, null, null, null)
        }

        fun bind(topic: Topic) {
            topicTitle.text = topic.title
            topicContainer.setBackgroundResource(TopicColorResolver.resolveTopicBackground(topic))
        }
    }
}