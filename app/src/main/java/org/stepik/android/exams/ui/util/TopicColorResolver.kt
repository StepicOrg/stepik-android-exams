package org.stepik.android.exams.ui.util

import android.support.annotation.DrawableRes
import org.stepik.android.exams.R
import org.stepik.android.exams.graph.model.Topic

object TopicColorResolver {
    private val gradients = arrayOf(
            R.drawable.lessons_header_bg
    )

    @DrawableRes
    fun resolveTopicBackgroundColor(topic: Topic): Int =
        gradients[hash(topic.id.hashCode())]

    private fun hash(x: Int): Int {
        var h = x
        h = h.shr(16).xor(h) * 0x45d9f3b
        h = h.shr(16).xor(h) * 0x45d9f3b
        h = h.shr(16).xor(h)
        return h % gradients.size
    }
}