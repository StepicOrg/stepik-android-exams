package org.stepik.android.exams.core.interactor

import io.reactivex.Scheduler
import org.stepik.android.exams.core.interactor.contacts.LessonNavigatorInteractor
import org.stepik.android.exams.core.presenter.LessonsPresenter
import org.stepik.android.exams.data.db.dao.NavigationDao
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.graph.Graph
import javax.inject.Inject

class LessonInteractor
@Inject
constructor(
        val graph: Graph<String>,
        private val navigationDao: NavigationDao,
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler,
        private val lessonsPresenter: LessonsPresenter
) : LessonNavigatorInteractor {

    override fun resolveNextLesson(lesson: Long) {
        var topicId: String
        navigationDao.findTopicByLessonId(lesson)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .doOnSuccess { id ->
                    if (id.isNotEmpty()) {
                        topicId = id as String
                        val nextTopic = graph[topicId]?.previous?.first()?.id ?: ""
                        if (nextTopic.isNotEmpty()) {
                            lessonsPresenter.tryJoinCourse(nextTopic)
                            lessonsPresenter.tryLoadLessons(nextTopic)
                        }
                    }
                }
                .subscribe()
    }

    override fun resolvePrevLesson(lesson: Long) {
        var topicId: String
        navigationDao.findTopicByLessonId(lesson)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .doOnSuccess { id ->
                    if (id.isNotEmpty()) {
                        topicId = id as String
                        val prevTopic = graph[topicId]?.neighbours?.first()?.id as String
                        if (prevTopic.isNotEmpty()) {
                            lessonsPresenter.tryJoinCourse(prevTopic)
                            lessonsPresenter.tryLoadLessons(prevTopic)
                        }
                    }
                }
                .subscribe()
    }
}