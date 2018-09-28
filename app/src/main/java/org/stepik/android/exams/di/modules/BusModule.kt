package org.stepik.android.exams.di.modules

import dagger.Module
import dagger.Provides
import io.reactivex.subjects.BehaviorSubject
import org.stepik.android.exams.di.AppSingleton


@Module
class BusModule {
    @Provides
    @AppSingleton
    internal fun provideSubject(): BehaviorSubject<Boolean> {
        return BehaviorSubject.create()
    }
}