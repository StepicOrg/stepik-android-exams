package org.stepik.android.exams.core.interactor.contacts

import io.reactivex.Single
import org.stepik.android.exams.data.model.ProgressesResponse

interface ProgressInteractor {
    fun getProgress(progress : Array<String>) : Single<ProgressesResponse>
}