package org.stepik.android.exams.core.interactor

import io.reactivex.Observable
import org.stepik.android.exams.core.interactor.contacts.NavigationInteractor
import org.stepik.android.exams.data.model.LessonTheoryWrapper
import org.stepik.android.exams.data.model.LessonType
import org.stepik.android.exams.data.repository.LessonsRepository
import javax.inject.Inject

class NavigationInteractorImpl
@Inject
constructor(
        private val graphInteractor: GraphInteractor,
        private val lessonsRepository: LessonsRepository
) : NavigationInteractor {
    //TODO rewrite navigation logic
    override fun resolveNextLesson(topicId: String, lessonId: Long, move: Boolean, lessons: LongArray): Observable<List<LessonTheoryWrapper>> =
            when (graphInteractor.isLastLessonInCurrentTopic(topicId, lessonId)) {
                true -> {
                    if (graphInteractor.hasNextTopic(topicId)) {
                        val nextTopic = graphInteractor.getNextTopic(topicId)
                        getTopic(nextTopic, move)
                    } else {
                        Observable.empty()
                    }
                }
                false -> {
                    val nextLesson = getNextLessonInTopic(lessonId, lessons)
                    getLessonInCurrentTopic(nextLesson, move)
                }
            }

    override fun resolvePrevLesson(topicId: String, lessonId: Long, move: Boolean, lessons: LongArray): Observable<List<LessonTheoryWrapper>> =
            when (graphInteractor.isFirstLessonInCurrentTopic(topicId, lessonId)) {
                true -> {
                    if (graphInteractor.hasPreviousTopic(topicId)) {
                        val nextTopic = graphInteractor.getPreviousTopic(topicId)
                        getTopic(nextTopic, move)
                    } else {
                        Observable.empty()
                    }
                }
                false -> {
                    val previousLesson = getPreviousLessonInTopic(lessonId, lessons)
                    getLessonInCurrentTopic(previousLesson, move)
                }
            }

    private fun getTopic(topicId: String, move: Boolean): Observable<List<LessonTheoryWrapper>> =
            if (topicId.isNotEmpty() && move) {
                lessonsRepository.loadLessonsByTopicId(topicId)
                        .ofType(LessonType.Theory::class.java)
                        .map { t: LessonType.Theory -> t.lessonTheoryWrapper }
                        .toList()
                        .toObservable()
            } else Observable.just(listOf(LessonTheoryWrapper()))

    private fun getLessonInCurrentTopic(lesson: Long, move: Boolean): Observable<List<LessonTheoryWrapper>> =
            if (lesson != 0L && move) {
                lessonsRepository.findLessonInDb(lesson)
                        .toList()
                        .toObservable()
            } else Observable.just(listOf(LessonTheoryWrapper()))

    private fun getNextLessonInTopic(lessonId: Long, lessons: LongArray): Long {
        lessons.forEachIndexed { index, lesson ->
            if (lesson == lessonId) return lessons[index + 1]
        }
        return 0
    }

    private fun getPreviousLessonInTopic(lessonId: Long, lessons: LongArray): Long {
        lessons.forEachIndexed { index, lesson ->
            if (lesson == lessonId) return lessons[index - 1]
        }
        return 0
    }
}