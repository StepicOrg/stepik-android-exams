package org.stepik.android.exams.core.presenter
import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.stepik.android.exams.App
import org.stepik.android.exams.di.AppCoreComponent

open class BasePresenter<View : MvpView> : MvpPresenter<View>() {

    private val disposables = CompositeDisposable()

    protected fun unsubscribeOnDestroy(disposable: Disposable) {
        disposables.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    fun getAppComponent(): AppCoreComponent = App.component()
}