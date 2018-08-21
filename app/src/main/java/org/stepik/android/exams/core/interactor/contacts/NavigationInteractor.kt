package org.stepik.android.exams.core.interactor.contacts

import io.reactivex.Observable
import org.stepik.android.exams.data.model.LessonTheoryWrapper

interface NavigationInteractor {
    fun resolveNextLesson(topicId: String, lesson: Int, move: Boolean): Observable<List<LessonTheoryWrapper>>
    fun resolvePrevLesson(topicId: String, lesson: Int, move: Boolean): Observable<List<LessonTheoryWrapper>>
}