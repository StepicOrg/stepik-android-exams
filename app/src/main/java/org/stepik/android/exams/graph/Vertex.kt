package org.stepik.android.exams.graph

import org.stepik.android.exams.graph.model.GraphLesson


data class Vertex<T>(val id: T, val title: String) {
    val children: MutableList<Vertex<T>> = mutableListOf()
    val parent: MutableList<Vertex<T>> = mutableListOf()
    val graphLessons: MutableList<GraphLesson> = mutableListOf()
}
