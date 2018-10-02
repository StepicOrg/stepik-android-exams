package org.stepik.android.exams.core.presenter

import org.stepik.android.exams.analytic.AmplitudeAnalytic
import org.stepik.android.exams.analytic.Analytic
import org.stepik.android.exams.core.presenter.contracts.LessonTrackingView
import org.stepik.android.model.Step
import javax.inject.Inject

class StepsTrackingPresenter
@Inject constructor(
        private val analytic: Analytic
) : PresenterBase<LessonTrackingView>() {

    fun trackStepType(step: Step) {
        analytic.reportAmplitudeEvent(AmplitudeAnalytic.Steps.STEP_OPENED, mapOf(
                AmplitudeAnalytic.Steps.Params.POSITION to step.position,
                AmplitudeAnalytic.Steps.Params.LESSON to step.lesson,
                AmplitudeAnalytic.Steps.Params.STEP to step.id
        ))
    }

    override fun destroy() {
    }
}
