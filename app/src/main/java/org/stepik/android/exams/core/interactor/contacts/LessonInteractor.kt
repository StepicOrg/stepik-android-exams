package org.stepik.android.exams.core.interactor.contacts

import io.reactivex.Maybe
import org.stepik.android.exams.core.interactor.LessonInteractorImpl

interface LessonInteractor {
    fun resolveNextLesson(topicId: String, lesson: Long, move: Boolean): Maybe<LessonInteractorImpl.LessonTheoryWrapper>
    fun resolvePrevLesson(topicId: String, lesson: Long, move: Boolean): Maybe<LessonInteractorImpl.LessonTheoryWrapper>
}