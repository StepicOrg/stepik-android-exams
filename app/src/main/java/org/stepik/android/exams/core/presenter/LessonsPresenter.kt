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
    private var lessonsList: LessonStepicResponse? = null
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
        Maybe.concat(loadTheoryLessonsLocal(), loadTheoryLessons()).take(1).subscribe({}, {
            onError()
        })
    }

    private fun findLessonsInDb(id: Long) =
            navigationDao.findLessonById(id)
                    .subscribeOn(backgroundScheduler)

    private fun saveLessonsToDb(list: List<org.stepik.android.exams.data.model.Lesson>) =
            disposable.add(Observable.fromCallable {
                val iterator = list.listIterator()
                val listToSave = mutableListOf<NavigationInfo>()
                findLessonsInDb(list.first().id)
                        .toSingle()
                        .subscribe({}, {
                            while (iterator.hasNext()) {
                                val next = iterator.next()
                                listToSave.add(NavigationInfo(id, next.id, next))
                            }
                            lessonsToDb(listToSave)
                        })
            }
                    .subscribeOn(backgroundScheduler)
                    .subscribe({}, {
                        onError()
                    }))

    private fun lessonsToDb(list: List<NavigationInfo>) =
            Maybe.fromCallable { navigationDao.insertLessons(list) }
                    .subscribeOn(backgroundScheduler)
                    .subscribe()


    private fun joinCourse(id: Long) =
            disposable.add(api.joinCourse(id)
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .subscribe({}, { onError() }))

    private fun loadTheoryLessons(): Maybe<Unit> {
        viewState = LessonsView.State.Loading
        return api.getLessons(getIdFromTheory(id))
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
                .map { l ->
                    val lessons = mutableListOf<org.stepik.android.exams.data.model.Lesson>()
                    parseLessons(id).first.forEach { theory ->
                        lessons.add(l.first { it.id == theory.id.toLong() })
                    }
                    lessonsList = LessonStepicResponse(null, lessons)
                    saveLessonsToDb(lessons)
                    onComplete(lessons)
                }
                .toMaybe()
    }


    private fun saveStepsToDb(steps: List<Step>) {
        val list = mutableListOf<StepInfo>()
        steps.forEach {
            list.add(StepInfo(it.id))
        }
        Maybe.fromCallable { stepDao.insertSteps(list) }
                .subscribeOn(backgroundScheduler)
                .subscribe()
    }

    private fun loadTheoryLessonsLocal() =
            navigationDao.findAllLessonsByTopicId(id)
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .filter { t: List<org.stepik.android.exams.data.model.Lesson> -> t.isNotEmpty() }
                    .map { list ->
                        lessonsList = LessonStepicResponse(null, list)
                        onComplete(list)
                    }


    private fun onComplete(list: List<org.stepik.android.exams.data.model.Lesson>) {
        view?.showLessons(list)
        viewState = LessonsView.State.Success
    }

    override fun attachView(view: LessonsView) {
        super.attachView(view)
        view.setState(viewState)
        lessonsList?.lessons.let {
            if (it?.isNotEmpty() == true)
                view.showLessons(it)
        }
    }

    private fun onError() {
        viewState = LessonsView.State.NetworkError
    }

    override fun destroy() {
        disposable.clear()
    }
}