package org.stepik.android.exams.data.repository

import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.data.db.dao.LessonDao
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.db.dao.TopicDao
import org.stepik.android.exams.data.db.data.LessonInfo
import org.stepik.android.exams.data.db.data.StepInfo
import org.stepik.android.exams.data.model.LessonTheoryWrapper
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
    fun getCoursesId(theoryId: String, type : GraphLesson.Type) =
            topicsDao.getTopicInfoByType(theoryId, type)

    fun tryLoadLessons(theoryId: String): Observable<List<LessonTheoryWrapper>> =
            loadTheoryLessonsLocal(theoryId).toObservable()
                    .switchIfEmpty(getCoursesId(theoryId, GraphLesson.Type.THEORY)
                            .flatMapObservable { loadTheoryLessons(theoryId, it.toLongArray()) })

    fun findLessonInDb(topicId: String, nextLesson: Long): Observable<LessonTheoryWrapper> =
            lessonDao.findLessonById(nextLesson)
                    .map { it -> LessonTheoryWrapper(topicId, it) }
                    .toObservable()

    private fun loadTheoryLessons(theoryId: String, lessonIds: LongArray): Observable<List<LessonTheoryWrapper>> {
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
                    Observable.just(lessons.map { LessonTheoryWrapper(theoryId, it) })
                }
                .doOnNext { wrappers ->
                    lessonDao.insertLessons(wrappers.map { LessonInfo(theoryId, it.lesson.lesson.id, it.lesson) })
                }
    }

    private fun loadTheoryLessonsLocal(theoryId: String) =
            lessonDao.findAllLessonsByTopicId(theoryId)
                    .filter { it.isNotEmpty() }
                    .map { list -> list.map { LessonTheoryWrapper(theoryId, it) } }
}