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
import org.stepik.android.exams.ui.activity.ListLessonActivity
import org.stepik.android.exams.ui.activity.TopicLessonsActivity
import org.stepik.android.exams.ui.activity.SplashActivity
import org.stepik.android.exams.ui.fragment.OnboardingFragment
import org.stepik.android.exams.ui.fragment.TopicsListFragment
import org.stepik.android.exams.ui.fragment.TrainingFragment

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

    fun inject(fragmentTopics: TopicsListFragment)

    fun inject(topicLessonsActivity: TopicLessonsActivity)

    fun inject(quizCardViewHolder: QuizCardViewHolder)

    fun inject(trainingFragment: TrainingFragment)

    fun inject(viewPushService: ViewPushService)

    fun inject(listLessonActivity: ListLessonActivity)
}