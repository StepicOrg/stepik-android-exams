package org.stepik.android.exams.graph

import org.stepik.android.exams.graph.model.Lesson


data class Vertex<T>(val id: T, val title: String) {
    val neighbours: MutableList<Vertex<T>> = mutableListOf()
    val previous: MutableList<Vertex<T>> = mutableListOf()
    val lessons: MutableList<Lesson> = mutableListOf()
}
