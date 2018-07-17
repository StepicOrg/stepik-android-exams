package org.stepik.android.exams.core.loader

import android.content.Context
import android.support.v4.content.Loader
import org.stepik.android.exams.core.presenter.Presenter
import javax.inject.Provider

class PresenterLoader<P : Presenter<*>>(
        appContext: Context,
        private val presenterProvider: Provider<P>
) : Loader<P>(appContext) {

    var presenter: P? = null
        private set

    override fun onStartLoading() {
        if (presenter != null) {
            deliverResult(presenter)
            return
        }

        forceLoad()
    }

    override fun onForceLoad() {
        presenter = presenterProvider.get()
        deliverResult(presenter)
    }

    override fun onReset() {
        presenter?.destroy()
        presenter = null
    }
}