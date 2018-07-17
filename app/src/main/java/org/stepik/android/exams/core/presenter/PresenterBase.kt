package org.stepik.android.exams.core.presenter

import android.support.annotation.CallSuper

abstract class PresenterBase<V> : Presenter<V> {
    @Volatile
    var view: V? = null
        private set

    @CallSuper
    override fun attachView(view: V) {
        val previousView = this.view

        if (previousView != null) {
            throw IllegalStateException("Previous view is not detached! previousView = " + previousView)
        }

        this.view = view
    }

    @CallSuper
    override fun detachView(view: V) {
        val previousView = this.view

        if (previousView === view) {
            this.view = null
        } else {
            throw IllegalStateException("Unexpected view! previousView = $previousView, getView to unbind = $view")
        }
    }
}