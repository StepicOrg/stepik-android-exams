package org.stepik.android.exams.core.interactor

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.toObservable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.core.interactor.contacts.LessonInteractor
import org.stepik.android.exams.data.db.dao.LessonDao
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.db.data.LessonInfo
import org.stepik.android.exams.data.db.data.StepInfo
import org.stepik.android.exams.data.model.LessonTheoryWrapper
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.graph.Graph
import org.stepik.android.exams.graph.model.GraphLesson
import org.stepik.android.model.Step
import javax.inject.Inject

class LessonInteractorImpl
@Inject
constructor(
        val graph: Graph<String>,
        private val lessonDao: LessonDao,
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        private val api: Api,
        private val stepDao: StepDao
) : LessonInteractor {
    private lateinit var lessonsList: MutableList<LessonWrapper>


    override fun resolveNextLesson(topicId: String, lesson: Long, move: Boolean): Maybe<LessonTheoryWrapper> {
        if (graph[topicId]?.parent?.isEmpty() == true &&
                graph[topicId]?.graphLessons?.last()?.id == lesson.toInt())
            return Maybe.empty()
        if (graph[topicId]?.graphLessons?.last()?.id != lesson.toInt()) {
            val theoryLessons = parseLessons(topicId).first
            val iterator = theoryLessons.iterator()
            var nextLesson = 0L
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (next.id == lesson.toInt())
                    nextLesson = iterator.next().id.toLong()
            }
            if (nextLesson != 0L && move) {
                return findLessonInDb(topicId, nextLesson)
            }
        } else {

            val nextTopic = graph[topicId]?.parent?.first()?.id ?: ""
            if (nextTopic.isNotEmpty() && move) {
                return loadLessons(nextTopic, first = true)
            }
        }
        return Maybe.just(LessonTheoryWrapper())
    }

    override fun resolvePrevLesson(topicId: String, lesson: Long, move: Boolean): Maybe<LessonTheoryWrapper> {
        if (graph[topicId]?.children?.isEmpty() == true &&
                graph[topicId]?.graphLessons?.first()?.id == lesson.toInt())
            return Maybe.empty()
        if (graph[topicId]?.graphLessons?.first()?.id != lesson.toInt()) {
            val theoryLessons = parseLessons(topicId).first
            val iterator = theoryLessons.listIterator()
            var nextLesson = 0L
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (next.id == lesson.toInt()) {
                    iterator.previous()
                    nextLesson = iterator.previous().id.toLong()
                    break
                }
            }
            if (nextLesson != 0L && move) {
                return findLessonInDb(topicId, nextLesson)
            }
        } else {
            val nextTopic = graph[topicId]?.children?.first()?.id ?: ""
            if (nextTopic.isNotEmpty() && move) {
                return loadLessons(nextTopic, first = false)
            }
        }
        return Maybe.just(LessonTheoryWrapper())
    }

    private fun loadLessons(topicId: String, first: Boolean): Maybe<LessonTheoryWrapper> {
        tryJoinCourse(topicId)
        return loadLessonFromDb(topicId, first)
    }

    private fun findLessonInDb(topicId: String, nextLesson: Long) =
            findLessonsInDb(nextLesson)
                    .map { it ->
                        return@map LessonTheoryWrapper(topicId, it)
                    }


    private fun loadLessonFromDb(topicId: String, first: Boolean): Maybe<LessonTheoryWrapper> =
            lessonDao.findAllLessonsByTopicId(topicId)
                    .subscribeOn(backgroundScheduler)
                    .flatMap { list ->
                        if (list.isEmpty())
                            return@flatMap loadLessonFromApi(topicId, first)
                        else {
                            lessonsList = list.toMutableList()
                            if (first)
                                return@flatMap Maybe.just(LessonTheoryWrapper(topicId, lessonsList.first()))
                            else return@flatMap Maybe.just(LessonTheoryWrapper(topicId, lessonsList.last()))
                        }
                    }

    private fun loadLessonFromApi(topicId: String, first: Boolean) =
            api.getLessons(getIdFromTheory(topicId))
                    .flatMapObservable {
                        it.lessons!!.toObservable()
                    }
                    .flatMap({
                        api.getSteps(*it.steps).toObservable()
                    }, { a, b -> a to b })
                    .map { (lesson, stepResponse) ->
                        return@map LessonWrapper(lesson, stepResponse.steps!!)
                    }
                    .doOnNext {
                        saveStepsToDb(it.stepsList)
                    }
                    .toList()
                    .subscribeOn(backgroundScheduler)
                    .map { lessonWrappers ->
                        val lessons = parseLessons(topicId).first.map { theory ->
                            lessonWrappers.first { it.lesson.id == theory.id.toLong() }
                        }
                        saveLessonsToDb(topicId, lessons)
                        if (first) LessonTheoryWrapper(topicId, lessonWrappers.first())
                        else LessonTheoryWrapper(topicId, lessonWrappers.last())
                    }.toMaybe()

    private fun getLessonsById(id: String) = graph[id]?.graphLessons

    private fun parseLessons(id: String): Pair<List<GraphLesson>,
            List<GraphLesson>> {
        val lessons = getLessonsById(id)
        val theory = mutableListOf<GraphLesson>()
        val practice = mutableListOf<GraphLesson>()
        if (lessons != null) {
            for (lesson in lessons) {
                when (lesson.type) {
                    "theory" -> theory.add(lesson)
                    else -> practice.add(lesson)
                }
            }
        }
        return Pair<List<GraphLesson>,
                List<GraphLesson>>(theory, practice)
    }

    private fun getUniqueCourses(graphLessons: List<GraphLesson>): MutableList<Long> {
        val uniqueCourses = mutableListOf<Long>()
        uniqueCourses.add(graphLessons.first { l -> l.course != 0L }.course)
        return uniqueCourses
    }

    private fun tryJoinCourse(id: String) {
        for (u in getUniqueCourses(parseLessons(id).first))
            joinCourse(u)
    }

    private fun joinCourse(id: Long) =
            api.joinCourse(id)
                    .subscribeOn(backgroundScheduler)
                    .subscribe({}, { })

    private fun getIdFromTheory(id: String) =
            parseLessons(id).first.map { it.id.toLong() }.toLongArray()


    private fun findLessonsInDb(id: Long) =
            lessonDao.findLessonById(id)
                    .subscribeOn(backgroundScheduler)

    private fun saveLessonsToDb(id: String, list: List<LessonWrapper>) =
            Completable.fromAction {
                lessonDao.insertLessons(list.map { LessonInfo(id, it.lesson.id, it) })
            }

    private fun saveStepsToDb(steps: List<Step>) =
            Completable.fromAction {
                stepDao.insertSteps(steps.map { StepInfo(it.id) })
            }
}