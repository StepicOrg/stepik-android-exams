package org.stepik.android.exams.data.repository

import io.reactivex.Completable
import io.reactivex.Maybe
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
import org.stepik.android.model.Step
import javax.inject.Inject

class StepsRepository
@Inject constructor(
        private val api: Api,
        private val lessonDao: LessonDao,
        private val stepDao: StepDao,
        private val topicsDao: TopicDao
) {
    companion object {
        const val type = "theory"
    }
    private lateinit var parsedLessons : List<Long>
    private fun getCoursesId(theoryId: String)  =
            topicsDao.getTopicInfoByType(theoryId, type)


    fun tryLoadLessons(theoryId: String) =
            loadTheoryLessonsLocal(theoryId).toObservable()
                    .switchIfEmpty(getCoursesId(theoryId).flatMapObservable { loadTheoryLessons(theoryId, it.toLongArray()) })

    fun findLessonInDb(topicId: String, nextLesson: Long) =
            lessonDao.findLessonById(nextLesson)
                    .map { it -> LessonTheoryWrapper(topicId, it) }
                    .toObservable()

    private fun loadTheoryLessons(theoryId: String, array : LongArray): Observable<List<LessonTheoryWrapper>> {
        parsedLessons = array.toList()
        return api.getLessons(array)
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
                    val lessons = parsedLessons.map { id ->
                        lessonWrappers.first { it.lesson.id == id }
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