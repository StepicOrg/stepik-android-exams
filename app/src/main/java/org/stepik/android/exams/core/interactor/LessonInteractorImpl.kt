package org.stepik.android.exams.core.interactor

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.toObservable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.core.interactor.contacts.LessonInteractor
import org.stepik.android.exams.data.db.dao.LessonDao
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.db.data.LessonInfo
import org.stepik.android.exams.data.db.data.StepInfo
import org.stepik.android.exams.data.model.Lesson
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.graph.Graph
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
    private lateinit var lessonsList: List<Lesson>

    data class LessonWrapper(val theoryId: String, val lesson: Lesson){
        constructor() : this("", Lesson())
    }


    override fun resolveNextLesson(topicId: String, lesson: Long, move : Boolean): Maybe<LessonWrapper> {
        if (graph[topicId]?.parent?.isEmpty() == true &&
                graph[topicId]?.lessons?.last()?.id == lesson.toInt())
            return Maybe.empty()
        if (graph[topicId]?.lessons?.last()?.id != lesson.toInt()) {
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
        return Maybe.just(LessonWrapper())
    }

    override fun resolvePrevLesson(topicId: String, lesson: Long, move : Boolean): Maybe<LessonWrapper> {
        if (graph[topicId]?.children?.isEmpty() == true &&
                graph[topicId]?.lessons?.first()?.id == lesson.toInt())
            return Maybe.empty()
        if (graph[topicId]?.lessons?.first()?.id != lesson.toInt()) {
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
        return Maybe.just(LessonWrapper())
    }

    private fun loadLessons(topicId: String, first: Boolean): Maybe<LessonWrapper> {
        tryJoinCourse(topicId)
        return loadLessonFromDb(topicId, first)
    }

    private fun findLessonInDb(topicId: String, nextLesson: Long) =
            findLessonsInDb(nextLesson)
                    .flatMap { it ->
                        return@flatMap Maybe.just(LessonWrapper(topicId, it))
                    }


    private fun loadLessonFromDb(id: String, first: Boolean): Maybe<LessonWrapper> =
            lessonDao.findAllLessonsByTopicId(id)
                    .subscribeOn(backgroundScheduler)
                    .flatMap { list ->
                        if (list.isEmpty())
                            return@flatMap loadLessonFromApi(id, first)
                        else {
                            lessonsList = list
                            if (first)
                                return@flatMap Maybe.just(LessonWrapper(id, lessonsList.first()))
                            else return@flatMap Maybe.just(LessonWrapper(id, lessonsList.last()))
                        }
                    }

    private fun loadLessonFromApi(id: String, first: Boolean) =
            api.getLessons(getIdFromTheory(id))
                    .flatMapObservable {
                        it.lessons!!.toObservable()
                    }
                    .flatMap({
                        api.getSteps(*it.steps).toObservable()
                    }, { a, b -> a to b })
                    .map { (lesson, stepResponse) ->
                        lesson.apply {
                            saveStepsToDb(stepResponse.steps!!)
                            stepsList = stepResponse.steps
                            stepsList?.last()?.is_last = true
                        }
                    }
                    .toList()
                    .subscribeOn(backgroundScheduler)
                    .doOnSuccess { l ->
                        val lessons = mutableListOf<Lesson>()
                        parseLessons(id).first.forEach { theory ->
                            lessons.add(l.first { it.id == theory.id.toLong() })
                        }
                        lessonsList = lessons
                        saveLessonsToDb(id, lessons)
                    }
                    .map {
                        if (first) LessonWrapper(id, it.first())
                        else LessonWrapper(id, it.last())
                    }
                    .toMaybe()


    private fun getLessonsById(id: String) = graph[id]?.lessons

    private fun parseLessons(id: String): Pair<List<org.stepik.android.exams.graph.model.Lesson>,
            List<org.stepik.android.exams.graph.model.Lesson>> {
        val lessons = getLessonsById(id)
        val theory = mutableListOf<org.stepik.android.exams.graph.model.Lesson>()
        val practice = mutableListOf<org.stepik.android.exams.graph.model.Lesson>()
        if (lessons != null) {
            for (lesson in lessons) {
                when (lesson.type) {
                    "theory" -> theory.add(lesson)
                    else -> practice.add(lesson)
                }
            }
        }
        return Pair<List<org.stepik.android.exams.graph.model.Lesson>,
                List<org.stepik.android.exams.graph.model.Lesson>>(theory, practice)
    }

    private fun getUniqueCourses(lessons: List<org.stepik.android.exams.graph.model.Lesson>): MutableList<Long> {
        val uniqueCourses = mutableListOf<Long>()
        uniqueCourses.add(lessons.first { l -> l.course != 0L }.course)
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

    private fun getIdFromTheory(id: String): LongArray {
        val theoryLessons = parseLessons(id).first
        val array = LongArray(theoryLessons.size)
        for (lesson in theoryLessons)
            array[theoryLessons.indexOf(lesson)] = lesson.id.toLong()
        return array
    }

    private fun findLessonsInDb(id: Long) =
            lessonDao.findLessonById(id)
                    .subscribeOn(backgroundScheduler)

    private fun saveLessonsToDb(id: String, list: List<Lesson>) =
            Observable.fromCallable {
                val iterator = list.listIterator()
                val listToSave = mutableListOf<LessonInfo>()
                findLessonsInDb(list.first().id)
                        .toSingle()
                        .subscribe({}, {
                            while (iterator.hasNext()) {
                                val next = iterator.next()
                                listToSave.add(LessonInfo(id, next.id, next))
                            }
                            lessonsToDb(listToSave)
                        })
            }
                    .subscribeOn(backgroundScheduler)
                    .subscribe({}, {
                    })


    private fun lessonsToDb(list: List<LessonInfo>) =
            Maybe.fromCallable { lessonDao.insertLessons(list) }
                    .subscribeOn(backgroundScheduler)
                    .subscribe()

    private fun saveStepsToDb(steps: List<Step>) {
        val list = mutableListOf<StepInfo>()
        steps.forEach {
            list.add(StepInfo(it.id))
        }
        Maybe.fromCallable { stepDao.insertSteps(list) }
                .subscribeOn(backgroundScheduler)
                .subscribe()
    }
}