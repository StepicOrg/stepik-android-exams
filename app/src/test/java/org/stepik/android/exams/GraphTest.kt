package org.stepik.android.exams
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.stepik.android.exams.graph.Graph

class GraphTest {
    val graph = Graph<Int>()
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
    }
    @Test
    fun BFSTest(){
        val vertexToVisit = graph.BFS(8)
        var output = String()
        vertexToVisit.forEach {output = output.plus(it.toString()).plus("\n")}
        Assert.assertEquals(output, "8\n6\n5\n4\n1\n3\n2\n")
    }
}