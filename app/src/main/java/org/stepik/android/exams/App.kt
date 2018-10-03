package org.stepik.android.exams

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import org.stepik.android.exams.di.AppCoreComponent
import org.stepik.android.exams.di.ComponentManager
import org.stepik.android.exams.di.DaggerAppCoreComponent

open class App : Application() {
    companion object {
        lateinit var app: App

        fun component() = app.component
        fun componentManager() = app.componentManager
        fun getAppContext(): Context = app.applicationContext
    }

    private lateinit var component: AppCoreComponent
    private lateinit var componentManager: ComponentManager

    override fun onCreate() {
        super.onCreate()
        app = this
        component = DaggerAppCoreComponent
                .builder()
                .context(applicationContext)
                .build()
        component.inject(this)
        componentManager = ComponentManager(component)

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

}
