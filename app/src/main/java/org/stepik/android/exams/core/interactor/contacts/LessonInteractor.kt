package org.stepik.android.exams.core.interactor.contacts

import io.reactivex.Maybe
import org.stepik.android.exams.data.model.LessonTheoryWrapper

interface LessonInteractor {
    fun resolveNextLesson(topicId: String, lesson: Long, move: Boolean): Maybe<LessonTheoryWrapper>
    fun resolvePrevLesson(topicId: String, lesson: Long, move: Boolean): Maybe<LessonTheoryWrapper>
}