package org.stepik.android.exams.core.interactor.contacts

import io.reactivex.Single

interface ProgressInteractor {
    fun loadTopicProgressFromDb(topicId: String): Single<Int>
    fun loadTopicProgressFromApi(topicId: String): Single<Int>
}