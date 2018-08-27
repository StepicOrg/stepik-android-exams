package org.stepik.android.exams
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.stepik.android.exams.graph.Graph

class GraphTest {
    private val graph = Graph<Int>()
    private val stringGraph = Graph<String>()
    @Before
    fun setUp() {
        graph.createVertex(1, "")
        graph.createVertex(2, "")
        graph.createVertex(3, "")
        graph.createVertex(4, "")
        graph.createVertex(5, "")
        graph.createVertex(6, "")
        graph.createVertex(7, "")
        graph.createVertex(8, "")
        graph.addEdge(1, 2)
        graph.addEdge(1, 3)
        graph.addEdge(1, 5)
        graph.addEdge(2, 3)
        graph.addEdge(3, 5)
        graph.addEdge(3, 4)
        graph.addEdge(4, 5)
        graph.addEdge(5, 6)
        graph.addEdge(4, 6)
        graph.addEdge(6, 7)
        graph.addEdge(6, 8)

        stringGraph.createVertex("0", "")
        stringGraph.createVertex("1", "")
        stringGraph.createVertex("2", "")
        stringGraph.createVertex("3", "")
        stringGraph.createVertex("4", "")
        stringGraph.createVertex("5", "")
        stringGraph.addEdge("5", "2")
        stringGraph.addEdge("5", "0")
        stringGraph.addEdge("4", "0")
        stringGraph.addEdge("4", "1")
        stringGraph.addEdge("2", "3")
        stringGraph.addEdge("3", "1")
    }

    @Test
    fun dfsTest(){
        Assert.assertEquals(listOf(8, 6, 5, 4, 3, 2, 1), graph.dfs(8))
    }

    @Test
    fun topologicalTest(){
        Assert.assertEquals(listOf("5", "4", "2", "3", "1", "0"), stringGraph.topologicalSort())
    }
}