package org.stepik.android.exams.graph

import java.util.*


data class Vertex<T>(val id: T){
    var neighbours: LinkedList<Vertex<T>?> = LinkedList()
    var previous: LinkedList<Vertex<T>?> = LinkedList()
    var path: LinkedList<Vertex<T>> = LinkedList()
}
