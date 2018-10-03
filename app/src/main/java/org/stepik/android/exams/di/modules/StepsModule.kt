package org.stepik.android.exams.di.modules

import dagger.Binds
import dagger.Module
import org.stepik.android.exams.core.interactor.NavigationInteractorImpl
import org.stepik.android.exams.core.interactor.contacts.NavigationInteractor
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
    fun provideNavigationInteractor(navigationInteractor: NavigationInteractorImpl): NavigationInteractor
}