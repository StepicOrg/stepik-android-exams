package org.stepik.android.exams.data.repository

import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.data.db.dao.GraphLessonDao
import javax.inject.Inject

class CoursesRepository
@Inject
constructor(
        private val stepicRestService: StepicRestService,
        private val graphLessonDao: GraphLessonDao
) {
    fun getLastStep() =
            stepicRestService.getUserCourses(1)
                    .flatMap { list -> Single.just(list.userCourse.map { it.course }) }.zipWith(graphLessonDao.getUniqueCourses())
                    .map { (userCourses, graphCourses) -> graphCourses.first { it in userCourses } }
                    .flatMap { stepicRestService.getCourses(1, longArrayOf(it)) }
                    .map { it.courses.first().lastStepId }
                    .flatMap { stepicRestService.getLastStepResponse(it) }
                    .map { it.lastSteps.first() }

}