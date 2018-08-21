package org.stepik.android.exams.core.interactor

import org.stepik.android.exams.core.interactor.contacts.LessonInteractor
import org.stepik.android.exams.data.repository.StepsRepository
import javax.inject.Inject

class LessonInteractorImpl
@Inject
constructor(
        private val repository: StepsRepository
) : LessonInteractor {
    override fun loadLessons(topicId: String) =
            repository.tryLoadLessons(topicId)

    override fun findLesson(topicId: String, lesson: Long) =
            repository.findLessonInDb(topicId, lesson)

    override fun parseLessons(topicId: String) =
            repository.parseLessons(topicId)

}