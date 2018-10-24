package org.stepik.android.exams.api

import io.reactivex.Completable
import io.reactivex.Single
import org.stepik.android.exams.data.model.UserCoursesResponse
import org.stepik.android.exams.data.model.*
import org.stepik.android.exams.data.model.AttemptRequest
import org.stepik.android.exams.data.model.AttemptResponse
import org.stepik.android.model.EnrollmentWrapper
import retrofit2.http.*

interface StepicRestService {
    @Headers("Content-LessonType: application/json")
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

    @GET("api/steps")
    fun getStepsByLessonId(
            @Query("lesson") lessonId: Long
    ): Single<StepResponse>

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
            @Query("attempt") attemptId: Long,
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

    @GET("api/units")
    fun getUnits(
            @Query("course") courseId: Long,
            @Query("lesson") lessonId: Long
    ): Single<UnitMetaResponse>

    @Headers("Content-LessonType: application/json")
    @POST("api/views")
    fun postViewed(
            @Body stepAssignment: ViewAssignmentWrapper
    ): Completable

    @GET("api/assignments")
    fun getAssignments(
            @Query("ids[]")assignmentsIds: LongArray
    ): Single<AssignmentResponse>

    @GET("api/user-courses")
    fun getUserCourses(
            @Query("page") page: Int
    ): Single<UserCoursesResponse>

    @GET("api/courses")
    fun getCourses(
            @Query("page") page: Int,
            @Query("ids[]") courseIds: LongArray
    ): Single<CoursesMetaResponse>

    @GET("api/last-steps/{lastStepId}")
    fun getLastStepResponse(
            @Path("lastStepId") lastStepId: String
    ): Single<LastStepResponse>
}