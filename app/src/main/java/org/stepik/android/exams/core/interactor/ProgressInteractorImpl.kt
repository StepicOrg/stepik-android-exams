package org.stepik.android.exams.core.interactor

import io.reactivex.Single
import org.stepik.android.exams.api.StepicRestService
import org.stepik.android.exams.core.interactor.contacts.ProgressInteractor
import org.stepik.android.exams.data.model.ProgressesResponse
import javax.inject.Inject

class ProgressInteractorImpl
@Inject
constructor(
        val stepicRestService: StepicRestService
) : ProgressInteractor {
    override fun getProgress(progress : Array<String>) : Single<ProgressesResponse> =
            stepicRestService.getProgresses(progress)
}