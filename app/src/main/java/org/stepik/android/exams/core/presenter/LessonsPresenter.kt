package org.stepik.android.exams.core.presenter

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.toObservable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.core.presenter.contracts.LessonsView
import org.stepik.android.exams.data.db.dao.NavigationDao
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.db.data.NavigationInfo
import org.stepik.android.exams.data.db.data.StepInfo
import org.stepik.android.exams.data.model.LessonStepicResponse
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.Graph
import org.stepik.android.exams.graph.model.Lesson
import java.util.*
import javax.inject.Inject

class LessonsPresenter
@Inject
constructor(
        private val graph: Graph<String>,
        private val api: Api,
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler,
        private val navigationDao: NavigationDao,
        private val stepDao: StepDao
) : PresenterBase<LessonsView>() {
    private var viewState: LessonsView.State = LessonsView.State.Idle
        set(value) {
            field = value
            view?.setState(value)
        }
    private lateinit var lessonsList: LessonStepicResponse
    private var listId: MutableList<LongArray> = mutableListOf()
    private var id: String = ""
    private var disposable = CompositeDisposable()

    init {
        viewState = LessonsView.State.FirstLoading
    }

    private fun getLessonsById(id: String) = graph[id]?.lessons

    private fun parseLessons(id: String): Pair<List<Lesson>,
            List<org.stepik.android.exams.graph.model.Lesson>> {
        val lessons = getLessonsById(id)
        val theory = mutableListOf<Lesson>()
        val practice = mutableListOf<Lesson>()
        if (lessons != null) {
            for (lesson in lessons) {
                when (lesson.type) {
                    "theory" -> theory.add(lesson)
                    else -> practice.add(lesson)
                }
            }
        }
        return Pair<List<Lesson>,
                List<Lesson>>(theory, practice)
    }

    private fun getUniqueCourses(lessons: List<Lesson>): MutableList<Long> {
        val uniqueCourses = mutableListOf<Long>()
        uniqueCourses.add(lessons.first { l -> l.course != 0L }.course)
        return uniqueCourses
    }

    private fun getIdFromTheory(id: String): LongArray {
        val theoryLessons = parseLessons(id).first
        val array = LongArray(theoryLessons.size)
        for (lesson in theoryLessons)
            array[theoryLessons.indexOf(lesson)] = lesson.id.toLong()
        return array
    }

    fun tryJoinCourse(id: String) {
        for (u in getUniqueCourses(parseLessons(id).first))
            joinCourse(u)
    }

    fun tryLoadLessons(id: String) {
        this.id = id
        loadTheoryLessonsLocal()
    }

    private fun findLessonsInDb(id: Long) =
            navigationDao.findLessonById(id)

    private fun saveLessonsToDb() =
            disposable.add(Observable.fromCallable {
                val iterator = lessonsList.lessons?.listIterator()
                while (iterator?.hasNext() == true) {
                    val next = iterator.next()
                    findLessonsInDb(next.id)
                            .toSingle()
                            .subscribe({}, {
                                navigationDao.insertLessons(NavigationInfo(id, next.id, next))
                            })

                }
            }
                    .subscribeOn(backgroundScheduler)
                    .subscribe())


    private fun joinCourse(id: Long) =
            disposable.add(api.joinCourse(id)
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .subscribe({}, { onError() }))

    private fun loadTheoryLessons() {
        viewState = LessonsView.State.Loading
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
                .observeOn(mainScheduler)
                .doOnError { viewState = LessonsView.State.NetworkError }
                .subscribe({ l ->
                    val lessons = mutableListOf<org.stepik.android.exams.data.model.Lesson>()
                    parseLessons(id).first.forEach { theory ->
                        lessons.add(l.first { it.id == theory.id.toLong() })
                    }
                    lessonsList = LessonStepicResponse(null, lessons)
                    onComplete()
                    saveLessonsToDb()
                }, {
                    viewState = LessonsView.State.NetworkError
                })
    }
    private fun saveStepsToDb(steps : List<Step>) {
        val list = mutableListOf<StepInfo>()
        steps.forEach {
            list.add(StepInfo(it.id))
        }
        Maybe.fromCallable { stepDao.insertSteps(list) }
                .subscribeOn(backgroundScheduler)
                .subscribe()
    }

    private fun loadTheoryLessonsLocal() =
            disposable.add(navigationDao.findAllLesson(id)
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .doOnSuccess { list ->
                        if (list.isEmpty()) loadTheoryLessons()
                        else {
                            lessonsList = LessonStepicResponse(null, list)
                            onComplete()
                        }
                    }
                    .subscribe())

    private fun onComplete() {
        view?.showLessons(lessonsList.lessons)
        viewState = LessonsView.State.Success
    }

    fun clearData() {
        listId.clear()
    }

    override fun attachView(view: LessonsView) {
        super.attachView(view)
        view.setState(viewState)
        if (listId.isNotEmpty()) {
            view.showLessons(lessonsList.lessons)
        }
    }

    private fun onError() {
        viewState = LessonsView.State.NetworkError
    }

    override fun destroy() {
        disposable.clear()
    }
}