package org.stepik.android.exams.di.network

import dagger.Binds
import dagger.Module
import org.stepik.android.exams.core.interactor.LessonInteractorImpl
import org.stepik.android.exams.core.interactor.contacts.LessonInteractor
import org.stepik.android.exams.di.AppSingleton
import org.stepik.android.exams.util.resolvers.StepTypeResolver
import org.stepik.android.exams.util.resolvers.StepTypeResolverImpl

@Module
interface StepsModule {
    @AppSingleton
    @Binds
    fun provideStepTypeResolver(stepTypeResolverImpl: StepTypeResolverImpl): StepTypeResolver

    @AppSingleton
    @Binds
    fun provideLessonInteractor(LessonInteractor: LessonInteractorImpl): LessonInteractor
}