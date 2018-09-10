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
    override fun resolveNextLesson(topicId: String, lessonId: Long, move: Boolean, lessons: LongArray): Observable<List<LessonTheoryWrapper>> {
        if (graph[topicId]?.parent?.isEmpty() == true &&
                graph[topicId]?.graphLessons?.last()?.id == lessonId)
            return Observable.empty()
        if (graph[topicId]?.graphLessons?.last()?.id != lessonId) {
            val iterator = lessons.iterator()
            var nextLesson = 0L
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (next == lessonId)
                    nextLesson = iterator.next()
            }
            if (nextLesson != 0L && move) {
                return stepsRepository.findLessonDb(topicId, nextLesson)
                        .toList()
                        .toObservable()
            }
        } else {
            val nextTopic = graph[topicId]?.parent?.first()?.id ?: ""
            if (nextTopic.isNotEmpty() && move) {
                return stepsRepository.loadLessonsByTopicId(nextTopic)
                        .ofType(LessonTheoryWrapper::class.java)
                        .toList()
                        .toObservable()
            }
        }
        return Observable.just(listOf(LessonTheoryWrapper()))
    }

    override fun resolvePrevLesson(topicId: String, lessonId: Long, move: Boolean, lessons: LongArray): Observable<List<LessonTheoryWrapper>> {
        if (graph[topicId]?.children?.isEmpty() == true &&
                graph[topicId]?.graphLessons?.first()?.id == lessonId)
            return Observable.empty()
        if (graph[topicId]?.graphLessons?.first()?.id != lessonId) {
            val iterator = lessons.asList().listIterator()
            var nextLesson = 0L
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (next == lessonId) {
                    iterator.previous()
                    nextLesson = iterator.previous()
                    break
                }
            }
            if (nextLesson != 0L && move) {
                return stepsRepository.findLessonDb(topicId, nextLesson)
                        .toList()
                        .toObservable()
            }
        } else {
            val nextTopic = graph[topicId]?.children?.first()?.id ?: ""
            if (nextTopic.isNotEmpty() && move) {
                return stepsRepository.loadLessonsByTopicId(nextTopic)
                        .ofType(LessonTheoryWrapper::class.java)
                        .toList()
                        .toObservable()
            }
        }
        return Observable.just(listOf(LessonTheoryWrapper()))
    }
}