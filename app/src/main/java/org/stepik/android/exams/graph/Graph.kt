package org.stepik.android.exams.graph

class Graph<T> {
    enum class Direction { UP, DOWN }

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

    private fun dfsSort(vertex: T, visited: MutableSet<T>, stack: MutableList<T>, direction: Direction) {
        visited.add(vertex)
        when (direction) {
            Direction.UP -> {
                vertices[vertex]?.parent?.forEach {
                    val vertNext: T = it.id
                    if (!visited.contains(vertNext)) {
                        dfsSort(vertNext, visited, stack, direction)
                    }
                }
            }
            Direction.DOWN -> {
                vertices[vertex]?.children?.forEach {
                    val vertNext: T = it.id
                    if (!visited.contains(vertNext)) {
                        dfsSort(vertNext, visited, stack, direction)
                    }
                }
            }
        }
        stack.add(vertex)
    }

    fun dfs(vertex: T): List<T> {
        val stack = mutableListOf<T>()
        dfsSort(vertex, mutableSetOf(), stack, direction = Direction.UP)
        return stack.reversed()
    }

    fun topologicalSort(): List<T> {
        val stack = mutableListOf<T>()
        val visited = mutableSetOf<T>()
        for (v in vertices)
            if (!visited.contains(v.key))
                dfsSort(v.key, visited, stack, direction = Direction.DOWN)
        return stack.reversed()
    }
}
