package org.stepik.android.exams.core.interactor.contacts

import io.reactivex.Observable
import org.stepik.android.exams.data.model.LessonTheoryWrapper
import org.stepik.android.exams.graph.model.GraphLesson


interface LessonInteractor {
    fun loadLessons(topicId: String): Observable<List<LessonTheoryWrapper>>
    fun findLesson(topicId: String, lesson: Long): Observable<LessonTheoryWrapper>
    fun parseLessons(topicId: String): List<GraphLesson>
}