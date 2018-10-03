package org.stepik.android.exams.di.modules

import dagger.Module
import dagger.Provides
import io.reactivex.subjects.BehaviorSubject
import org.stepik.android.exams.di.AppSingleton
import org.stepik.android.exams.di.qualifiers.LessonProgressUpdatesBus

@Module
class BusModule {
    @Provides
    @AppSingleton
    @LessonProgressUpdatesBus
    internal fun provideLessonProgressUpdatesBus(): BehaviorSubject<Long> =
            BehaviorSubject.create()
}