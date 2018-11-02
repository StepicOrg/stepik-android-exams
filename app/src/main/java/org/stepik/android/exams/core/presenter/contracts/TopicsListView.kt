package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.data.model.LessonTheoryWrapper
import org.stepik.android.exams.data.model.TopicAdapterItem
import org.stepik.android.exams.graph.model.Topic

interface TopicsListView {
    fun setState(state: State)
    fun continueEducation(lessonTheoryWrapper: LessonTheoryWrapper, position : Long)
    sealed class State {
        object Idle : State()
        object Loading : State()
        class Success(val topics: List<TopicAdapterItem>, val topic: Topic) : State()
        class Refreshing(val topics: List<TopicAdapterItem>, val topic: Topic) : State()
        object NetworkError : State()
    }
}