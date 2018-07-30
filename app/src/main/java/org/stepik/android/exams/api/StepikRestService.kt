package org.stepik.android.exams.api

import io.reactivex.Completable
import io.reactivex.Single
import org.stepik.android.exams.data.model.EnrollmentWrapper
import org.stepik.android.exams.data.model.LessonStepicResponse
import org.stepik.android.exams.data.model.StepResponse
import org.stepik.android.exams.web.AttemptRequest
import org.stepik.android.exams.web.AttemptResponse
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

    @GET("api/attempts")
    fun getExistingAttempts(
            @Query("step") stepId: Long,
            @Query("user") userId: Long
    ): Single<AttemptResponse>

    @POST("api/attempts")
    fun createNewAttempt(
            @Body attemptRequest: AttemptRequest
    ): Single<AttemptResponse>
}