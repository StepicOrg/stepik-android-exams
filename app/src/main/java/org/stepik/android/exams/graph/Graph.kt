package org.stepik.android.exams.graph

import java.util.*
import kotlin.collections.ArrayList

class Graph<T> {
    private val vertices = mutableMapOf<T, Vertex<T>?>()
    private val vertexToVisit : ArrayList<T?> = ArrayList()
    fun createVertex(id: T, title : String) {
        vertices[id] = Vertex(id, title)
    }

    fun addEdge(src: T, dest: T) {
        val start = vertices[src]
        val end = vertices[dest]
        start?.neighbours?.add(end)
        end?.previous?.add(start)
    }

    fun getVertex(vert: T): Vertex<T>? = vertices[vert]

    fun BFS(vert: T?) : ArrayList<T?>{
        val visited = mutableMapOf<T?, Boolean>()
        val queue: LinkedList<T?> = LinkedList()
        visited[vert] = true
        queue.add(vert)
        while (queue.size > 0) {
            val vertKey = queue.poll()
            vertexToVisit.add(vertKey)
            vertices[vertKey]?.previous?.listIterator()?.forEach {
                val vertNext = it?.id
                if (visited[vertNext] == null)
                {
                    visited[vertNext] = true
                    queue.add(vertNext)
                }
            }
        }
        return vertexToVisit
    }
}
