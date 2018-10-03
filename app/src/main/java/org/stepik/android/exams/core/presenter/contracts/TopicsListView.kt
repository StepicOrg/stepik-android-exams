package org.stepik.android.exams.core.presenter.contracts

import org.stepik.android.exams.data.model.TopicAdapterItem

interface TopicsListView {
    fun setState(state: State)
    sealed class State {
        object Idle : State()
        object Loading : State()
        class Success(val topics: List<TopicAdapterItem>) : State()
        class Refreshing(val topics: List<TopicAdapterItem>) : State()
        class ProgressUpdate(val progress: Int) : State()
        object NetworkError : State()
    }
}