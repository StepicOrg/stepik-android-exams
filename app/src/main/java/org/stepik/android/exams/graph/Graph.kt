package org.stepik.android.exams.graph

class Graph<T> {
    private val vertices = mutableMapOf<T, Vertex<T>>()
    fun createVertex(id: T, title: String) {
        vertices[id] = Vertex(id, title)
    }

    fun addEdge(src: T, dest: T) {
        val start = vertices[src] ?: throw IllegalArgumentException("No such vertex with id $src")
        val end = vertices[dest] ?: throw IllegalArgumentException("No such vertex with id $src")
        start.children.add(end)
        end.parent.add(start)
    }

    operator fun get(vert: T) = vertices[vert]

    private fun dfsSortAdjustment(vertex: T, visited : MutableSet<T>, stack : MutableList<T>) {
        visited.add(vertex)
            vertices[vertex]?.children?.forEach {
                val vertNext: T = it.id
                if (!visited.contains(vertNext)) {
                    dfsSortAdjustment(vertNext, visited, stack)
                }
            }
        stack.add(vertex)
    }
    private fun dfsSortParent(vertex: T, visited : MutableSet<T>, stack : MutableList<T>) {
        visited.add(vertex)
        vertices[vertex]?.parent?.forEach {
            val vertNext: T = it.id
            if (!visited.contains(vertNext)) {
                dfsSortParent(vertNext, visited, stack)
            }
        }
        stack.add(vertex)
    }

    fun dfs(vertex: T): List<T> {
        val stack = mutableListOf<T>()
        dfsSortParent(vertex, mutableSetOf(), stack)
        return stack.reversed()
    }

    fun topologicalSort() : List<T> {
        val stack = mutableListOf<T>()
        val visited = mutableSetOf<T>()
        for (v in vertices)
            if (!visited.contains(v.key))
                dfsSortAdjustment(v.key, visited, stack)
        return stack.reversed()
    }
}
