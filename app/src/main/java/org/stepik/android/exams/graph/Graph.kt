package org.stepik.android.exams.graph

import java.util.*
import kotlin.collections.ArrayList

class Graph<T> {
    private val vertices = mutableMapOf<T, Vertex<T>>()
    fun createVertex(id: T, title: String) {
        vertices[id] = Vertex(id, title)
    }

    fun addEdge(src: T, dest: T) {
        val start = vertices[src] ?: throw IllegalArgumentException("No such vertex with id $src")
        val end = vertices[dest] ?: throw IllegalArgumentException("No such vertex with id $src")
        start.neighbours.add(end)
        end.previous.add(start)
    }
    operator fun get(vert : T) = vertices[vert]

    fun bfs(vertex: T): List<T> {
        val visited = mutableSetOf<T>()
        val queue: Queue<T> = ArrayDeque()
        visited.add(vertex)
        queue.add(vertex)
        while (queue.size > 0) {
            vertices[queue.poll()]?.previous?.forEach {
                val vertNext: T = it.id
                if (!visited.contains(vertNext)) {
                    visited.add(vertNext)
                    queue.add(vertNext)
                }
            }
        }
        return visited.toList()
    }
}
