package org.stepik.android.exams.core.presenter

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.core.presenter.contracts.LessonsView
import org.stepik.android.exams.data.db.dao.NavigationDao
import org.stepik.android.exams.data.db.data.NavigationInfo
import org.stepik.android.exams.data.model.LessonStepicResponse
import org.stepik.android.exams.data.model.StepResponse
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
        private val navigationDao: NavigationDao
) : PresenterBase<LessonsView>() {

    private var viewState: LessonsView.State = LessonsView.State.Idle
        set(value) {
            field = value
            view?.setState(value)
        }

    private var theoryLessons: LinkedList<Lesson> = LinkedList()

    private var practiceLessons: LinkedList<Lesson> = LinkedList()

    lateinit var lessonsList: LessonStepicResponse

    var listId: MutableList<LongArray> = mutableListOf()

    var id: String = ""

    private var disposable = CompositeDisposable()

    init {
        viewState = LessonsView.State.FirstLoading
    }

    private fun getLessonsById(id: String) = graph[id]?.lessons

    private fun parseLessons(id: String): MutableSet<Long> {
        val lessons = getLessonsById(id)
        val uniqueCourses: MutableSet<Long> = mutableSetOf()
        if (lessons != null) {
            for (lesson in lessons) {
                when (lesson.type) {
                    "theory" -> theoryLessons.add(lesson)
                    else -> practiceLessons.add(lesson)
                }
                if (lesson.course != 0L)
                    uniqueCourses.add(lesson.course)
            }
        }
        return uniqueCourses
    }

    private fun getIdFromTheory(): LongArray {
        val array = LongArray(theoryLessons.size)
        for (lesson in theoryLessons)
            array[theoryLessons.indexOf(lesson)] = lesson.id.toLong()
        return array
    }

    private fun loadLessons() =
            api.getLessons(getIdFromTheory())
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .doOnError { viewState = LessonsView.State.NetworkError }
                    .doOnSuccess { response ->
                        lessonsList = response
                        lessonsList.lessons?.forEach {
                            listId.add(it.steps)
                        }
                    }.toCompletable()

    private fun loadSteps(step: LongArray, id: Int) =
            api.getSteps(step)
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .doOnError { viewState = LessonsView.State.NetworkError }
                    .doOnSuccess { response ->
                        lessonsList.lessons?.get(id)?.stepsList = response.steps?.toMutableList()
                        lessonsList.lessons?.get(id)?.stepsList?.last()?.is_last = true
                    }

    private fun onError() {
        viewState = LessonsView.State.NetworkError
    }

    fun joinCourses(id: String) {
        for (u in parseLessons(id))
            joinCourse(u)
    }

    private fun findLessonsInDb(id: Long) =
            navigationDao.findLessonById(id)?.lesson

    private fun saveLessonsToDb() =
            Observable.fromCallable {
                val iterator = lessonsList.lessons?.listIterator()
                while (iterator?.hasNext() == true) {
                    val next = iterator.next()
                    if (findLessonsInDb(next.id) != null)
                        return@fromCallable
                    val nextIndex = iterator.nextIndex().toLong().plus(1)
                    val prevIndex = iterator.previousIndex().toLong()
                    navigationDao.insertLessons(NavigationInfo(next.id, next, nextIndex, prevIndex))
                }
            }
                    .subscribeOn(backgroundScheduler)
                    .subscribe()


    private fun joinCourse(id: Long) =
            disposable.add(api.joinCourse(id)
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .subscribe({}, { onError() }))

    fun loadTheoryLessons() {
        viewState = LessonsView.State.Loading
        disposable.add(loadLessons()
                .andThen { _ ->
                    val observableList: MutableList<Observable<StepResponse>>? = mutableListOf()
                    listId.forEach { list ->
                        observableList?.add(loadSteps(list, listId.indexOf(list)).toObservable())
                    }
                    Observable.zip(observableList) {}
                            .doOnComplete {
                                view?.showLessons(lessonsList.lessons)
                                viewState = LessonsView.State.Success
                                saveLessonsToDb()
                            }
                            .subscribe()
                }
                .doOnError { onError() }
                .subscribe())
    }

    fun clearData() {
        listId.clear()
        theoryLessons.clear()
        practiceLessons.clear()
    }

    override fun attachView(view: LessonsView) {
        super.attachView(view)
        view.setState(viewState)
        if (listId.isNotEmpty()) {
            view.showLessons(lessonsList.lessons)
            saveLessonsToDb()
        }
    }

    override fun destroy() {
        disposable.clear()
    }
}