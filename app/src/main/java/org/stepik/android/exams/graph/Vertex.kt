package org.stepik.android.exams.graph

import org.stepik.android.exams.graph.model.Lesson


data class Vertex<T>(val id: T, val title: String) {
    val children: MutableList<Vertex<T>> = mutableListOf()
    val parent: MutableList<Vertex<T>> = mutableListOf()
    val lessons: MutableList<Lesson> = mutableListOf()
}
