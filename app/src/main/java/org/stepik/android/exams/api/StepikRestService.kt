package org.stepik.android.exams.api

import io.reactivex.Single
import org.stepik.android.exams.data.model.LessonStepicResponse
import org.stepik.android.exams.data.model.StepResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface StepikRestService {
    @GET("api/steps")
    fun getSteps(
            @Query("ids[]") steps: LongArray
    ): Single<StepResponse>

    @GET("api/lessons")
    fun getLessons(
            @Query("ids[]") lessons: LongArray
    ): Single<LessonStepicResponse>
}