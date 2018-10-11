package org.stepik.android.exams.graph

import org.stepik.android.exams.graph.model.GraphLesson
import org.stepik.android.exams.graph.model.Topic


data class Vertex<T>(val id: T, val topic: Topic) {
    val children: MutableList<Vertex<T>> = mutableListOf()
    val parent: MutableList<Vertex<T>> = mutableListOf()
    val graphLessons: MutableList<GraphLesson> = mutableListOf()
}
