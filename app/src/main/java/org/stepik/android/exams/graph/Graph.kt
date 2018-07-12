package org.stepik.android.exams.graph

import java.util.*
import kotlin.collections.ArrayList

class Graph<T> {
    private var vertices = mutableMapOf<T, Vertex<T>?>()
    var vertexToVisit : ArrayList<T?> = ArrayList()
    fun createVertex(id: T, title : T) {
        vertices[id] = Vertex(id, title)
    }

    fun addEdge(src: T, dest: T) {
        val start = vertices[src]
        val end = vertices[dest]
        start?.neighbours?.add(end)
        end?.previous?.add(start)
    }

    fun getEdges(id: T) = vertices[id]?.neighbours

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
