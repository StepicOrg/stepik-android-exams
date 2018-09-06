package org.stepik.android.exams.data.repository

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.data.db.dao.LessonDao
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.db.dao.TopicDao
import org.stepik.android.exams.data.db.data.LessonInfo
import org.stepik.android.exams.data.db.data.StepInfo
import org.stepik.android.exams.data.model.LessonPracticeWrapper
import org.stepik.android.exams.data.model.LessonTheoryWrapper
import org.stepik.android.exams.data.model.LessonType
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.graph.model.GraphLesson
import javax.inject.Inject

class StepsRepository
@Inject
constructor(
        private val api: Api,
        private val lessonDao: LessonDao,
        private val stepDao: StepDao,
        private val topicsDao: TopicDao
) {
    private fun getTheoryCoursesId(theoryId: String) : Maybe<List<Long>> =
            topicsDao.getTopicInfoByType(theoryId, GraphLesson.Type.THEORY)

    fun getPracticeCoursesId(theoryId: String): Maybe<LessonType.Practice> =
            topicsDao.getTopicInfoByType(theoryId, GraphLesson.Type.PRACTICE)
                    .filter { it.isNotEmpty() }
                    .map { topics -> LessonType.Practice(LessonPracticeWrapper(theoryId, topics.first())) }

    fun loadTheoryLesson(theoryId: String) : Observable<LessonType.Theory> =
            loadTheoryLessonsLocal(theoryId)
                    .switchIfEmpty(getTheoryCoursesId(theoryId)
                            .flatMapObservable { loadTheoryLessons(theoryId, it.toLongArray()) })

    fun tryLoadLessons(theoryId: String): Observable<LessonType> =
            Observable.merge(loadTheoryLesson(theoryId),
                    getPracticeCoursesId(theoryId).toObservable())

    fun findLessonInDb(topicId: String, nextLesson: Long): Observable<LessonTheoryWrapper> =
            lessonDao.findLessonById(nextLesson)
                    .map { it -> LessonTheoryWrapper(topicId, it) }
                    .toObservable()

    private fun loadTheoryLessons(theoryId: String, lessonIds: LongArray): Observable<LessonType.Theory> {
        return api.getLessons(lessonIds)
                .flatMapObservable {
                    it.lessons!!.toObservable()
                }
                .flatMap({ lesson ->
                    api.getSteps(*lesson.steps).doOnSuccess { response ->
                        stepDao.insertSteps(response.steps!!.map { StepInfo(it.id) })
                    }.toObservable()
                }, { a, b -> a to b })
                .map { (lesson, stepResponse) ->
                    LessonWrapper(lesson, stepResponse.steps!!)
                }
                .toList()
                .flatMapObservable { lessonWrappers ->
                    val lessons = lessonIds.map { id ->
                        lessonWrappers.first { it.lesson.id == id }
                    }
                    lessonDao.insertLessons(lessons.map { LessonInfo(theoryId, it.lesson.id, it) })
                    Observable.fromIterable(lessons.map { LessonType.Theory(LessonTheoryWrapper(theoryId, it)) })
                }
    }

    private fun loadTheoryLessonsLocal(theoryId: String): Observable<LessonType.Theory> =
            lessonDao.findAllLessonsByTopicId(theoryId)
                    .filter { it.isNotEmpty() }
                    .flatMapObservable { list -> Observable.fromIterable(list.map { LessonType.Theory(LessonTheoryWrapper(theoryId, it)) }) }
}