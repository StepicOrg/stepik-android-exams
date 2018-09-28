package org.stepik.android.exams.data.repository

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.zipWith
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.core.interactor.contacts.GraphInteractor
import org.stepik.android.exams.data.db.dao.LessonDao
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.db.dao.TopicDao
import org.stepik.android.exams.data.db.mapping.toEntity
import org.stepik.android.exams.data.db.mapping.toObject
import org.stepik.android.exams.data.model.LessonPracticeWrapper
import org.stepik.android.exams.data.model.LessonTheoryWrapper
import org.stepik.android.exams.data.model.LessonType
import org.stepik.android.exams.graph.model.GraphLesson
import org.stepik.android.exams.util.PercentUtil
import javax.inject.Inject

class LessonsRepository
@Inject
constructor(
        private val api: Api,
        private val lessonDao: LessonDao,
        private val topicsDao: TopicDao,
        private val stepsDao: StepDao,
        private val graphInteractor: GraphInteractor,
        private val progressRepository: ProgressRepository
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
        return api.getLessons(lessonIds)
                .flatMapObservable {
                    it.lessons!!.toObservable()
                }
                .flatMap({ lesson ->
                    api.getSteps(*lesson.steps).doOnSuccess { response ->
                        val stepList = response.steps!!
                        stepsDao.insertSteps(stepList.map { it.toEntity() })
                    }.toObservable().zipWith(getTheoryCourseIdByLessonIdFromDb(lesson.id).toObservable())
                }, { a, b -> a to b })
                .map { (lesson, response) ->
                    LessonTheoryWrapper(lesson, response.first.steps!!, topicId, response.second)
                }
                .toList()
                .flatMapObservable { lessonWrappers ->
                    val lessons = lessonIds.map { id ->
                        lessonWrappers.first { it.lesson.id == id }
                    }
                    lessonDao.insertLessons(lessons.map { it.lesson.toEntity() })
                    Observable.fromIterable(lessons.map { LessonType.Theory(it) })
                }
    }

    fun findLessonInDb(nextLesson: Long): Observable<LessonTheoryWrapper> =
            lessonDao.findLessonById(nextLesson)
                    .map { it.toObject() }
                    .toObservable()

    private fun getAllTheoryCoursesIdFromDb(topicId: String): Maybe<List<Long>> =
            topicsDao.getTopicByType(topicId, GraphLesson.Type.THEORY)

    private fun getPracticeCoursesIdFromDb(topicId: String): Maybe<LessonType.Practice> =
            topicsDao.getTopicByType(topicId, GraphLesson.Type.PRACTICE)
                    .filter { it.isNotEmpty() }
                    .map { topics -> LessonType.Practice(LessonPracticeWrapper(topicId, topics.first())) }

    private fun loadTheoryLessonsFromDb(topicId: String): Observable<LessonType.Theory> =
            lessonDao.findAllLessonsByTopicId(topicId)
                    .filter { lessonList ->  lessonList.isNotEmpty() }
                    .flattenAsObservable { lessonList -> lessonList.map { LessonType.Theory(it.toObject()) } }

    private fun getTheoryCourseIdByLessonIdFromDb(lessonId: Long): Maybe<Long> =
            topicsDao.getCourseInfoByLessonId(lessonId)

    fun resolveTimeToComplete(topicId: String) : Observable<Long> =
            loadTheoryLessonByTopicId(topicId)
                    .ofType(LessonType.Theory::class.java)
                    .map { it.lessonTheoryWrapper.lesson.timeToComplete }
                    .reduce(0L){
                        t1, t2 -> t1 + t2
                    }
                    .toObservable()

    fun loadStepProgressApi(topicId: String) : Observable<Int> =
            loadLessonsByTopicId(topicId)
                    .flatMapSingle { progressRepository.getStepsProgressByTopic(topicId) }
                    .flatMapSingle { progressRepository.getProgressApi( it.toTypedArray()) }
                    .map {progressResponse -> progressResponse.progresses.map { it.nStepsPassed.toFloat()/it.nSteps } }
                    .map { PercentUtil.formatPercent(it.sum(), it.size.toFloat()) }

    fun loadStepProgressFromDb(topicId: String) : Observable<Int> =
            loadLessonsByTopicId(topicId)
                    .flatMapSingle { progressRepository.getStepsProgressLocalByTopic(topicId) }
                    .map { progressList -> PercentUtil.formatPercent(progressList.count { it }.toFloat(), progressList.size.toFloat()) }
}