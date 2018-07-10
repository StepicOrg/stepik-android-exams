package org.stepik.android.exams
import org.junit.Assert
import org.junit.Test
import org.stepik.android.exams.graph.Graph

class GraphTest {

    @Test
    fun GraphTest(){
    val graph = Graph<Int>()
        graph.createVertex(89)
        graph.createVertex(56)
        graph.createVertex(14)
        graph.createVertex(42)
        graph.addEdge(14, 89)
    }
}