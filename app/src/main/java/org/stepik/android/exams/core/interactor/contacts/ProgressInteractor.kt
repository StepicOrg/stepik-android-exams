package org.stepik.android.exams.core.interactor.contacts

import io.reactivex.Single

interface ProgressInteractor {
    fun loadStepProgressFromDb(topicId: String): Single<Int>
    fun loadStepProgressFromApi(topicId: String) : Single<Int>
}