package org.stepik.android.exams.core.presenter
import io.reactivex.Observable
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import org.stepik.android.exams.api.StepikRestService
import org.stepik.android.exams.graph.Graph
import org.stepik.android.exams.core.presenter.contracts.StudyView
import org.stepik.android.exams.data.model.LessonStepicResponse
import org.stepik.android.exams.data.model.StepResponse
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.model.Lesson
import org.stepik.android.exams.util.then
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

    private lateinit var id : String

    private fun getLessonsById()
    = graph.getVertex(id)?.lessons

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
    private fun getIDFromTheory() : LongArray{
        val array : LongArray = LongArray(theoryLessons.size)
        for (lesson in theoryLessons)
            array[theoryLessons.indexOf(lesson)] = lesson.id.toLong()
        return array
    }
    private fun loadTheoryLessons() {
        loadLessons(getIDFromTheory())
/*                .subscribe {
                    response ->
                    var steps : LongArray
                    loadLessons(response.lessons)
                }
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)*/
    }
    private fun loadSteps(steps : LongArray) =
            stepikService.getStepsReactive(steps)

    private fun loadLessons(lessons : LongArray) =
            stepikService.getLessonsRx(lessons)

    override fun destroy() {
    }
}