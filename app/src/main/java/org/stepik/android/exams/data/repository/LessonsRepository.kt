package org.stepik.android.exams.data.repository

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.toObservable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.core.interactor.GraphInteractor
import org.stepik.android.exams.data.db.dao.LessonDao
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.db.dao.TopicDao
import org.stepik.android.exams.data.db.data.LessonInfo
import org.stepik.android.exams.data.db.data.StepInfo
import org.stepik.android.exams.data.model.LessonPracticeWrapper
import org.stepik.android.exams.data.model.LessonTheoryWrapper
import org.stepik.android.exams.data.model.LessonType
import org.stepik.android.exams.graph.model.GraphLesson
import javax.inject.Inject

class LessonsRepository
@Inject
constructor(
        private val api: Api,
        private val lessonDao: LessonDao,
        private val stepDao: StepDao,
        private val topicsDao: TopicDao,
        private val graphInteractor: GraphInteractor
) {
    private val topicsList = graphInteractor.getTopicsList()

    private fun loadTheoryLessonByTopicId(topicId: String): Observable<LessonType.Theory> =
            loadTheoryLessonsFromDb(topicId)
                    .switchIfEmpty(getTheoryCoursesIdFromDb(topicId)
                            .flatMapObservable { loadTheoryLessonsApi(topicId, it.toLongArray()) })

    fun loadLessonsByTopicId(topicId: String): Observable<LessonType> =
            Observable.merge(loadTheoryLessonByTopicId(topicId),
                    getPracticeCoursesIdFromDb(topicId).toObservable())

    fun loadAllTheoryLessons(): Observable<List<LessonType.Theory>> =
            Observable.merge(topicsList.map { loadTheoryLessonByTopicId(it) }).toList().toObservable()

    fun loadAllPracticeLessons(): Observable<List<LessonType.Practice>> =
            Observable.merge(topicsList.map { getPracticeCoursesIdFromDb(it).toObservable() }).toList().toObservable()

    fun loadAllLessons(): Observable<Pair<List<LessonType.Theory>, List<LessonType.Practice>>> =
            Observables.zip(loadAllTheoryLessons(), loadAllPracticeLessons())

    private fun loadTheoryLessonsApi(topicId: String, lessonIds: LongArray): Observable<LessonType.Theory> {
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
                    LessonTheoryWrapper(lesson, stepResponse.steps!!, topicId)
                }
                .toList()
                .flatMapObservable { lessonWrappers ->
                    val lessons = lessonIds.map { id ->
                        lessonWrappers.first { it.lesson.id == id }
                    }
                    lessonDao.insertLessons(lessons.map { LessonInfo(topicId, it.lesson.id, it) })
                    Observable.fromIterable(lessons.map { LessonType.Theory(it) })
                }
    }

    fun findLessonInDb(nextLesson: Long): Observable<LessonTheoryWrapper> =
            lessonDao.findLessonById(nextLesson)
                    .toObservable()

    private fun getTheoryCoursesIdFromDb(topicId: String): Maybe<List<Long>> =
            topicsDao.getTopicInfoByType(topicId, GraphLesson.Type.THEORY)

    private fun getPracticeCoursesIdFromDb(topicId: String): Maybe<LessonType.Practice> =
            topicsDao.getTopicInfoByType(topicId, GraphLesson.Type.PRACTICE)
                    .filter { it.isNotEmpty() }
                    .map { topics -> LessonType.Practice(LessonPracticeWrapper(topicId, topics.first())) }

    private fun loadTheoryLessonsFromDb(topicId: String): Observable<LessonType.Theory> =
            lessonDao.findAllLessonsByTopicId(topicId)
                    .filter { lessonList -> lessonList.isNotEmpty() }
                    .flattenAsObservable { lessonList -> lessonList.map { LessonType.Theory(it) } }
}