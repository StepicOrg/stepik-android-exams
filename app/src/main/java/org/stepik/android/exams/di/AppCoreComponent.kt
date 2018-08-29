package org.stepik.android.exams.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import org.stepik.android.exams.App
import org.stepik.android.exams.adaptive.ui.adapter.QuizCardViewHolder
import org.stepik.android.exams.core.services.ViewPushService
import org.stepik.android.exams.di.network.GraphModule
import org.stepik.android.exams.di.network.NetworkModule
import org.stepik.android.exams.di.network.RoomModule
import org.stepik.android.exams.di.network.StepsModule
import org.stepik.android.exams.ui.activity.LessonsActivity
import org.stepik.android.exams.ui.activity.SplashActivity
import org.stepik.android.exams.ui.activity.TopicsListActivity
import org.stepik.android.exams.ui.fragment.OnboardingFragment

@AppSingleton
@Component(modules = [AppCoreModule::class, NetworkModule::class, GraphModule::class, RoomModule::class, StepsModule::class])
interface AppCoreComponent {

    @Component.Builder
    interface Builder {
        fun build(): AppCoreComponent

        @BindsInstance
        fun context(context: Context): Builder

    }

    fun loginComponentBuilder(): LoginComponent.Builder

    fun stepComponentBuilder(): StepComponent.Builder

    fun adaptiveComponentBuilder(): AdaptiveComponent.Builder

    fun inject(activity: SplashActivity)

    fun inject(fragment: OnboardingFragment)

    fun inject(app: App)

    fun inject(activityTopics: TopicsListActivity)

    fun inject(lessonsActivity: LessonsActivity)

    fun inject(quizCardViewHolder: QuizCardViewHolder)

    fun inject(viewPushService: ViewPushService)
}