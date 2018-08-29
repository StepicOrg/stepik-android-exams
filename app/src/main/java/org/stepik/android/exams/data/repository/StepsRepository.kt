package org.stepik.android.exams.data.repository

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.toObservable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.core.presenter.contracts.LessonsView.Type
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
    private fun getTheoryCoursesId(theoryId: String) =
            topicsDao.getTopicInfoByType(theoryId, GraphLesson.Type.THEORY)

    private fun getPracticeCoursesId(theoryId: String): Maybe<List<Type.Practice>> =
            topicsDao.getTopicInfoByType(theoryId, GraphLesson.Type.PRACTICE)
                    .map { topics -> topics.map { Type.Practice(it) } }

    fun tryLoadLessons(theoryId: String): Observable<List<Type>> =
            loadTheoryLessonsLocal(theoryId).toObservable()
                    .switchIfEmpty(getTheoryCoursesId(theoryId)
                            .flatMapObservable { loadTheoryLessons(theoryId, it.toLongArray()) })
                    .zipWith(
                            getPracticeCoursesId(theoryId).toObservable(), BiFunction { t1, t2 -> t1 + t2 })

    fun findLessonInDb(topicId: String, nextLesson: Long): Observable<LessonTheoryWrapper> =
            lessonDao.findLessonById(nextLesson)
                    .map { it -> LessonTheoryWrapper(topicId, it) }
                    .toObservable()

    private fun loadTheoryLessons(theoryId: String, lessonIds: LongArray): Observable<List<Type.Theory>> {
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
                    Observable.just(lessons.map { Type.Theory(LessonTheoryWrapper(theoryId, it)) })
                }
                .doOnNext { wrappers ->
                    lessonDao.insertLessons(wrappers.map { LessonInfo(theoryId, it.lessonTheoryWrapper.lesson.lesson.id, it.lessonTheoryWrapper.lesson) })
                }
    }

    private fun loadTheoryLessonsLocal(theoryId: String): Maybe<List<Type.Theory>> =
            lessonDao.findAllLessonsByTopicId(theoryId)
                    .filter { it.isNotEmpty() }
                    .map { list -> list.map { Type.Theory(LessonTheoryWrapper(theoryId, it)) } }
}