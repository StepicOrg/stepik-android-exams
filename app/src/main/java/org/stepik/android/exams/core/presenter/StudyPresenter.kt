package org.stepik.android.exams.core.presenter
import io.reactivex.Observable
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.stepik.android.exams.api.StepikRestService
import org.stepik.android.exams.graph.Graph
import org.stepik.android.exams.core.presenter.contracts.StudyView
import org.stepik.android.exams.data.model.LessonStepicResponse
import org.stepik.android.exams.data.model.StepResponse
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.model.Lesson
import java.util.*
import javax.inject.Inject

class StudyPresenter
@Inject
constructor(
        private val graph: Graph<String>,
        private val stepikService: StepikRestService,
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler
) : PresenterBase<StudyView>() {

    private var theoryLessons : LinkedList<Lesson> = LinkedList()

    private var practiceLessons : LinkedList<Lesson> = LinkedList()

    private var id : String = ""

    private fun getLessonsById() = graph[id]?.lessons

    private var stepsArray = LongArray(0)

    lateinit var lessonsList : LessonStepicResponse

    private fun parseLessons(){
        val lessons = getLessonsById()
        if (lessons != null) {
            for (lesson in lessons)
                when(lesson.type){
                    "theory" -> theoryLessons.add(lesson)
                    else -> practiceLessons.add(lesson)
                }
        }
    }

    private fun getIdFromTheory() : LongArray{
        val array = LongArray(theoryLessons.size)
        for (lesson in theoryLessons)
            array[theoryLessons.indexOf(lesson)] = lesson.id.toLong()
        return array
    }
    fun loadLessons() =
        stepikService
                .getLessons(getIdFromTheory())
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe { response ->
                    lessonsList = response
                    val listId: LinkedList<LongArray> = LinkedList()
                    lessonsList.lessons?.forEach {
                        listId.add(it.steps)
                    }
                    listId.forEach {
                        stepsArray += it
                    }
                }
    fun loadSteps() =
            stepikService
                    .getSteps(stepsArray)
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .subscribe { response ->
                        lessonsList.lessons?.forEach { l->
                            response.steps?.forEach { s ->
                                if (s.lesson == l.id)
                                    l.stepsList.add(s)
                            }
                        }
                    }

    fun loadTheoryLessons(id : String) {
        this.id = id
        parseLessons()
        var disposable : Disposable = loadLessons().also {loadSteps()}
    }


    override fun destroy() {
    }
}