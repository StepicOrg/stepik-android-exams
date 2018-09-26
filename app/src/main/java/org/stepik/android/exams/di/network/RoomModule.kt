package org.stepik.android.exams.di.network

import android.content.Context
import dagger.Module
import dagger.Provides
import org.stepik.android.exams.data.db.AppDatabase
import org.stepik.android.exams.data.db.dao.*
import org.stepik.android.exams.di.AppSingleton


@Module
class RoomModule {

    @Provides
    @AppSingleton
    fun providesRoomDatabase(context: Context): AppDatabase {
        return AppDatabase.createPersistentDatabase(context)
    }

    @Provides
    @AppSingleton
    fun providesNavigationDao(app: AppDatabase): LessonDao {
        return app.lessonDao()
    }

    @Provides
    @AppSingleton
    fun providesTopicDao(app: AppDatabase): TopicDao {
        return app.topicDao()
    }

    @Provides
    @AppSingleton
    fun providesSubmissionDao(app: AppDatabase): SubmissionEntityDao =
            app.submissionEntityDao()


    @Provides
    @AppSingleton
    fun provideProgressDao(app: AppDatabase) : ProgressDao =
            app.progressDao()

    @Provides
    @AppSingleton
    fun provideStepDao(app: AppDatabase) : StepDao =
            app.stepDao()
}