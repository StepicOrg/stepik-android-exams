package org.stepik.android.exams.adaptive.listeners

import org.stepik.android.model.adaptive.Reaction


interface AdaptiveReactionListener {
    fun createReaction(lessonId: Long, reaction: Reaction)
}
