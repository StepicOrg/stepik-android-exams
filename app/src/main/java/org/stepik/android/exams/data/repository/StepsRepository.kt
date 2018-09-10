package org.stepik.android.exams.data.repository

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
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
        private val topicsDao: TopicDao,
        private val topicsRepository: TopicsRepository
) {
    private val topicsList = topicsRepository.getTopicsList()
    private fun getTheoryCoursesId(theoryId: String): Maybe<List<Long>> =
            topicsDao.getTopicInfoByType(theoryId, GraphLesson.Type.THEORY)

    private fun getPracticeCoursesId(theoryId: String): Maybe<LessonType.Practice> =
            topicsDao.getTopicInfoByType(theoryId, GraphLesson.Type.PRACTICE)
                    .filter { it.isNotEmpty() }
                    .map { topics -> LessonType.Practice(LessonPracticeWrapper(theoryId, topics.first())) }

    private fun loadTheoryLessonByTopicId(theoryId: String): Observable<LessonType.Theory> =
            loadTheoryLessonsDb(theoryId)
                    .switchIfEmpty(getTheoryCoursesId(theoryId)
                            .flatMapObservable { loadTheoryLessonsApi(theoryId, it.toLongArray()) })

    fun loadLessonsByTopicId(topicId: String): Observable<LessonType> =
            Observable.merge(loadTheoryLessonByTopicId(topicId),
                    getPracticeCoursesId(topicId).toObservable())

    fun loadAllTheoryLessons() : Observable<List<LessonType.Theory>> =
            Observable.merge(topicsList.map { loadTheoryLessonByTopicId(it) }).toList().toObservable()

    fun loadAllPracticeLessons() : Observable<List<LessonType.Practice>> =
            Observable.merge(topicsList.map { getPracticeCoursesId(it).toObservable() }).toList().toObservable()

    fun loadAllLessons() =
            Observables.zip(loadAllTheoryLessons(), loadAllPracticeLessons())

    fun findLessonDb(topicId: String, nextLesson: Long): Observable<LessonTheoryWrapper> =
            lessonDao.findLessonById(nextLesson)
                    .map { it -> LessonTheoryWrapper(topicId, it) }
                    .toObservable()

    private fun loadTheoryLessonsApi(theoryId: String, lessonIds: LongArray): Observable<LessonType.Theory> {
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

    private fun loadTheoryLessonsDb(theoryId: String): Observable<LessonType.Theory> =
            lessonDao.findAllLessonsByTopicId(theoryId)
                    .filter { it.isNotEmpty() }
                    .flatMapObservable { list -> Observable.fromIterable(list.map { LessonType.Theory(LessonTheoryWrapper(theoryId, it)) }) }
}