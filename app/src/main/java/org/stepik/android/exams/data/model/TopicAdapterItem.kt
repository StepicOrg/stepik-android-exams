package org.stepik.android.exams.data.model

import org.stepik.android.exams.graph.model.Topic

data class TopicAdapterItem(val topic : Topic, val timeToComplete : Long, val progress : String)