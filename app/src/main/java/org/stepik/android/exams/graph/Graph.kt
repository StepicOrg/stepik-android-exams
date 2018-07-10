package org.stepik.android.exams.graph

class Graph<T> {
    private var vertices = mutableMapOf<T, Vertex<T>?>()

    fun createVertex(id : T){
        vertices[id] = Vertex(id)
    }

    fun addEdge(src: T, dest: T) {
        val start = vertices[src]
        val end = vertices[dest]
        start?.neighbours?.add(end)
        end?.previous?.add(start)
    }

    fun getEdges(id : T) = vertices[id]?.neighbours

    fun getVertex(vert: T): Vertex<T>? {
        return vertices[vert]
    }

}