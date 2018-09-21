package org.stepik.android.exams.core.interactor

import org.stepik.android.exams.api.StepicRestService
import javax.inject.Inject

class ProgressInteractor
@Inject
constructor(
        val stepicRestService: StepicRestService
){
    fun getProgress(progress : Array<String>) =
            stepicRestService.getProgresses(progress)
}