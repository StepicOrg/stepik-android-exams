package org.stepik.android.exams.ui.util

import android.support.annotation.DrawableRes
import org.stepik.android.exams.R
import org.stepik.android.exams.graph.model.Topic

object TopicColorResolver {
    private val gradients = arrayOf(
            R.drawable.topic_background_1,
            R.drawable.topic_background_2,
            R.drawable.topic_background_3,
            R.drawable.topic_background_4
    )

    /**
     * returns @DrawableRes for topic background
     */
    @DrawableRes
    fun resolveTopicBackground(topic: Topic): Int =
        gradients[hash(Math.abs(topic.id.hashCode()))]

    private fun hash(x: Int): Int {
        var h = x
        h = h.shr(16).xor(h) * 0x45d9f3b
        h = h.shr(16).xor(h) * 0x45d9f3b
        h = h.shr(16).xor(h)
        return h % gradients.size
    }
}