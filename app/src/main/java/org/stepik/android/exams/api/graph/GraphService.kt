package org.stepik.android.exams.api.graph

import io.reactivex.Single
import org.stepik.android.exams.graph.model.GraphData
import retrofit2.http.GET


interface GraphService {
    @GET("example.json?dl=1")
    fun getPosts(): Single<GraphData>
}