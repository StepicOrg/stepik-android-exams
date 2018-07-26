package org.stepik.android.exams.core.presenter

import android.app.PendingIntent.getActivity
import android.view.View
import android.view.ViewGroup
import org.stepik.android.exams.core.presenter.contracts.StepsView
import org.stepik.android.exams.data.model.Step

class StepsPresenter(val stepViewContainer: ViewGroup, val step: Step?)
    : PresenterBase<StepsView>() {
    override fun attachView(view: StepsView) {
        super.attachView(view)
        fillTextDelegate()
    }

    fun fillTextDelegate(){
        view?.setHeader(stepViewContainer, step)
        view?.updateCommentState(stepViewContainer, step)
    }

    override fun detachView(view: StepsView) {
        super.detachView(view)
    }
    override fun destroy() {

    }
}