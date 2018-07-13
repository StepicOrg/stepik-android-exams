package org.stepik.android.exams.graph

import org.stepik.android.exams.graph.model.Lesson
import java.util.*


data class Vertex<T>(val id: T, val title : String){
    var neighbours: LinkedList<Vertex<T>?> = LinkedList()
    var previous: LinkedList<Vertex<T>?> = LinkedList()
    var lessons : LinkedList<Lesson> = LinkedList()
}
