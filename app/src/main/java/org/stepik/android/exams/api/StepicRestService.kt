package org.stepik.android.exams.api

import io.reactivex.Completable
import io.reactivex.Single
import org.stepik.android.exams.data.model.*
import org.stepik.android.exams.web.AttemptRequest
import org.stepik.android.exams.web.AttemptResponse
import org.stepik.android.model.EnrollmentWrapper
import retrofit2.http.*

interface StepicRestService {
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

    @POST("api/submissions")
    fun createSubmission(
            @Body submissionRequest: SubmissionRequest
    ): Completable

    @GET("api/submissions")
    fun getSubmissions(
            @Query("attempt") attempt_id: Long,
            @Query("order") desc: String
    ): Single<SubmissionResponse>

    @GET("api/progresses")
    fun getProgresses(
            @Query("ids[]") progresses: Array<String>
    ): Single<ProgressesResponse>

    @GET("api/recommendations")
    fun getNextRecommendations(
            @Query("course") courseId: Long,
            @Query("count") count: Int
    ): Single<RecommendationsResponse>

    @POST("api/recommendation-reactions")
    fun createRecommendationReaction(
            @Body reactionsRequest: RecommendationReactionsRequest
    ): Completable
}