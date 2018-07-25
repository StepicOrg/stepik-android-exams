package org.stepik.android.exams.core.presenter

import org.stepik.android.exams.core.presenter.contracts.StepsView
import org.stepik.android.exams.data.model.Step

class StepsPresenter(private val step: Step?)
    : PresenterBase<StepsView>() {
    override fun attachView(view: StepsView) {
        super.attachView(view)
        view.initialize()
        view.setHeader(step)
    }

    override fun detachView(view: StepsView) {
        super.detachView(view)
    }
    override fun destroy() {

    }
}