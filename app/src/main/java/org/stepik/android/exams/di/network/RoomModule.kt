package org.stepik.android.exams.di.network

import android.content.Context
import dagger.Module
import dagger.Provides
import org.stepik.android.exams.data.db.AppDatabase
import org.stepik.android.exams.data.db.dao.LessonDao
import org.stepik.android.exams.data.db.dao.StepDao
import org.stepik.android.exams.data.db.dao.TopicDao
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
    fun providesStepDao(app: AppDatabase): StepDao {
        return app.stepDao()
    }

    @Provides
    @AppSingleton
    fun providesNavigationDao(app: AppDatabase): LessonDao {
        return app.navigationDao()
    }

    @Provides
    @AppSingleton
    fun providesTopicDao(app: AppDatabase): TopicDao {
        return app.topicDao()
    }
}