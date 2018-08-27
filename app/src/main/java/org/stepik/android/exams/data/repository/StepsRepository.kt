package org.stepik.android.exams.data.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.data.db.dao.LessonDao
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.db.data.LessonInfo
import org.stepik.android.exams.data.db.data.StepInfo
import org.stepik.android.exams.data.model.LessonTheoryWrapper
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.graph.Graph
import org.stepik.android.exams.graph.model.GraphLesson
import org.stepik.android.model.Step
import javax.inject.Inject

class StepsRepository
@Inject constructor(
        private val graph: Graph<String>,
        private val api: Api,
        private val lessonDao: LessonDao,
        private val stepDao: StepDao
) {
    private fun tryJoinCourses(topidId: String): Completable =
            Completable.concat(getUniqueCourses(getTheoryLessonsByTopicId(topidId)).map(api::joinCourse))


    private fun getLessonsByTopicId(topicId: String) = graph[topicId]?.graphLessons

    fun getTheoryLessonsByTopicId(topicId: String): List<GraphLesson> =
            getLessonsByTopicId(topicId)!!.filter { it.type == "theory" }

    private fun getUniqueCourses(graphLessons: List<GraphLesson>): Set<Long> {
        val uniqueCourses = mutableSetOf<Long>()
        uniqueCourses.addAll(graphLessons.map { it.course })
        return uniqueCourses
    }

    private fun getIdFromTheory(id: String): LongArray =
            getTheoryLessonsByTopicId(id).map(GraphLesson::id).toLongArray()

    fun tryLoadLessons(theoryId: String): Observable<List<LessonTheoryWrapper>> =
            loadTheoryLessonsLocal(theoryId).toObservable()
                    .switchIfEmpty(tryJoinCourses(theoryId).andThen(loadTheoryLessons(theoryId)))

    fun findLessonInDb(topicId: String, nextLesson: Long): Observable<LessonTheoryWrapper> =
            lessonDao.findLessonById(nextLesson)
                    .map { it -> LessonTheoryWrapper(topicId, it) }
                    .toObservable()

    private fun loadTheoryLessons(theoryId: String): Observable<List<LessonTheoryWrapper>> {
        return api.getLessons(getIdFromTheory(theoryId))
                .flatMapObservable {
                    it.lessons!!.toObservable()
                }
                .flatMap({
                    api.getSteps(*it.steps).toObservable()
                }, { a, b -> a to b })
                .map { (lesson, stepResponse) ->
                    LessonWrapper(lesson, stepResponse.steps!!).apply {
                        saveStepsToDb(stepResponse.steps)
                    }
                }
                .toList()
                .flatMapObservable { lessonWrappers ->
                    val lessons = getTheoryLessonsByTopicId(theoryId).map { theory ->
                        lessonWrappers.first { it.lesson.id == theory.id }
                    }
                    saveLessonsToDb(theoryId, lessons)
                    Observable.just(lessons.map { LessonTheoryWrapper(theoryId, it) })
                }
    }

    private fun saveLessonsToDb(id: String, list: List<LessonWrapper>) =
            Completable.fromAction {
                lessonDao.insertLessons(list.map { LessonInfo(id, it.lesson.id, it) })
            }.subscribe()

    private fun saveStepsToDb(steps: List<Step>) =
            Completable.fromAction {
                stepDao.insertSteps(steps.map { StepInfo(it.id) })
            }.subscribe()

    private fun loadTheoryLessonsLocal(theoryId: String) =
            lessonDao.findAllLessonsByTopicId(theoryId)
                    .filter { t: List<LessonWrapper> -> t.isNotEmpty() }
                    .map { list -> list.map { LessonTheoryWrapper(theoryId, it) } }
}