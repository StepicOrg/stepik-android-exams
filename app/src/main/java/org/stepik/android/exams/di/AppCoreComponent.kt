package org.stepik.android.exams.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import org.stepik.android.exams.App
import org.stepik.android.exams.di.network.GraphModule
import org.stepik.android.exams.di.network.NetworkModule
import org.stepik.android.exams.ui.activity.SplashActivity
import org.stepik.android.exams.ui.activity.ListActivity
import org.stepik.android.exams.ui.fragment.OnboardingFragment

@AppSingleton
@Component(modules = [AppCoreModule::class, NetworkModule::class, GraphModule::class])
interface AppCoreComponent {

    @Component.Builder
    interface Builder {
        fun build(): AppCoreComponent

        @BindsInstance
        fun context(context: Context): Builder
    }

    fun loginComponentBuilder(): LoginComponent.Builder

    fun inject(activity: SplashActivity)

    fun inject(fragment: OnboardingFragment)

    fun inject(app: App)

    fun inject(activity: ListActivity)
}