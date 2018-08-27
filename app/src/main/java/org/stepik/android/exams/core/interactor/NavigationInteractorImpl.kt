package org.stepik.android.exams.core.interactor

import io.reactivex.Observable
import org.stepik.android.exams.core.interactor.contacts.NavigationInteractor
import org.stepik.android.exams.data.model.LessonTheoryWrapper
import org.stepik.android.exams.data.repository.StepsRepository
import org.stepik.android.exams.graph.Graph
import javax.inject.Inject

class NavigationInteractorImpl
@Inject
constructor(
        val graph: Graph<String>,
        private val stepsRepository: StepsRepository
) : NavigationInteractor {
    override fun resolveNextLesson(topicId: String, lesson: Long, move: Boolean): Observable<List<LessonTheoryWrapper>> {
        if (graph[topicId]?.parent?.isEmpty() == true &&
                graph[topicId]?.graphLessons?.last()?.id == lesson)
            return Observable.empty()
        if (graph[topicId]?.graphLessons?.last()?.id != lesson) {
            val theoryLessons = stepsRepository.getTheoryLessonsByTopicId(topicId)
            val iterator = theoryLessons.iterator()
            var nextLesson = 0L
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (next.id == lesson)
                    nextLesson = iterator.next().id
            }
            if (nextLesson != 0L && move) {
                return stepsRepository.findLessonInDb(topicId, nextLesson)
                        .toList()
                        .toObservable()
            }
        } else {
            val nextTopic = graph[topicId]?.parent?.first()?.id ?: ""
            if (nextTopic.isNotEmpty() && move) {
                return stepsRepository.tryLoadLessons(nextTopic)
            }
        }
        return Observable.just(listOf(LessonTheoryWrapper()))
    }

    override fun resolvePrevLesson(topicId: String, lesson: Long, move: Boolean): Observable<List<LessonTheoryWrapper>> {
        if (graph[topicId]?.children?.isEmpty() == true &&
                graph[topicId]?.graphLessons?.first()?.id == lesson)
            return Observable.empty()
        if (graph[topicId]?.graphLessons?.first()?.id != lesson) {
            val theoryLessons = stepsRepository.getTheoryLessonsByTopicId(topicId)
            val iterator = theoryLessons.listIterator()
            var nextLesson = 0L
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (next.id == lesson) {
                    iterator.previous()
                    nextLesson = iterator.previous().id
                    break
                }
            }
            if (nextLesson != 0L && move) {
                return stepsRepository.findLessonInDb(topicId, nextLesson)
                        .toList()
                        .toObservable()
            }
        } else {
            val nextTopic = graph[topicId]?.children?.first()?.id ?: ""
            if (nextTopic.isNotEmpty() && move) {
                return stepsRepository.tryLoadLessons(nextTopic)
            }
        }
        return Observable.just(listOf(LessonTheoryWrapper()))
    }
}