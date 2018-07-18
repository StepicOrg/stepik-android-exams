package org.stepik.android.exams.api

import io.reactivex.Completable
import io.reactivex.Single
import org.stepik.android.exams.data.model.EnrollmentWrapper
import org.stepik.android.exams.data.model.LessonStepicResponse
import org.stepik.android.exams.data.model.StepResponse
import retrofit2.http.*

interface StepikRestService {
    @Headers("Content-Type: application/json")
    @POST("api/enrollments")
    fun joinCourse(
            @Body enrollmentCourse: EnrollmentWrapper
    ): Completable

    @GET("api/steps")
    fun getSteps(
            @Query("ids[]") steps: LongArray
    ): Single<StepResponse>

    @GET("api/lessons")
    fun getLessons(
            @Query("ids[]") lessons: LongArray
    ): Single<LessonStepicResponse>
}