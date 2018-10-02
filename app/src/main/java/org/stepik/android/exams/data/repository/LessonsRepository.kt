package org.stepik.android.exams.data.repository

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.zipWith
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
import org.stepik.android.exams.data.model.StepResponse
import org.stepik.android.exams.graph.model.GraphLesson
import org.stepik.android.model.Lesson
import org.stepik.android.model.Step
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
                    .switchIfEmpty(getAllTheoryCoursesIdFromDb(topicId)
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
        return loadLessonsApi(lessonIds)
                .flatMap({ lesson ->
                    loadAndSaveStepsByLesson(lesson)
                            .zipWith(getTheoryCourseIdByLessonIdFromDb(lesson.id).toObservable())
                }, { a, b -> a to b })
                .map { (lesson, response) ->
                    LessonTheoryWrapper(lesson, response.first.steps!!, topicId, response.second)
                }
                .toList()
                .flatMapObservable { lessonWrappers ->
                    val lessons = sortLessons(lessonIds, lessonWrappers)
                    insertLessons(lessons, topicId)
                    Observable.fromIterable(lessons.map { LessonType.Theory(it) })
                }
    }

    private fun loadLessonsApi(lessonIds: LongArray): Observable<Lesson> =
            api.getLessons(lessonIds)
                    .flatMapObservable {
                        it.lessons!!.toObservable()
                    }

    private fun sortLessons(lessonIds: LongArray, lessonWrappers: List<LessonTheoryWrapper>): List<LessonTheoryWrapper> =
            lessonIds.map { id ->
                lessonWrappers.first { it.lesson.id == id }
            }

    private fun loadAndSaveStepsByLesson(lesson: Lesson) : Observable<StepResponse> =
            api.getSteps(*lesson.steps).doOnSuccess { response ->
                saveStepsToDb(response.steps!!)
            }.toObservable()

    private fun insertLessons(lessons : List<LessonTheoryWrapper>, topicId: String) =
            lessonDao.insertLessons(lessons.map { LessonInfo(topicId, it.lesson.id, it) })

    private fun saveStepsToDb(steps: List<Step>)  =
            stepDao.insertSteps(steps.map { StepInfo(it.id) })

    fun findLessonInDb(nextLesson: Long): Observable<LessonTheoryWrapper> =
            lessonDao.findLessonById(nextLesson)
                    .toObservable()

    private fun getAllTheoryCoursesIdFromDb(topicId: String): Maybe<List<Long>> =
            topicsDao.getTopicInfoByType(topicId, GraphLesson.Type.THEORY)

    private fun getPracticeCoursesIdFromDb(topicId: String): Maybe<LessonType.Practice> =
            topicsDao.getTopicInfoByType(topicId, GraphLesson.Type.PRACTICE)
                    .filter { it.isNotEmpty() }
                    .map { topics -> LessonType.Practice(LessonPracticeWrapper(topicId, topics.first())) }

    private fun getTheoryCourseIdByLessonIdFromDb(lessonId: Long): Maybe<Long> =
            topicsDao.getCourseInfoByLessonId(lessonId)

    private fun loadTheoryLessonsFromDb(topicId: String): Observable<LessonType.Theory> =
            lessonDao.findAllLessonsByTopicId(topicId)
                    .filter { lessonList -> lessonList.isNotEmpty() }
                    .flattenAsObservable { lessonList -> lessonList.map { LessonType.Theory(it) } }
}