package org.stepik.android.exams.di.network

import android.content.Context
import dagger.Module
import dagger.Provides
import org.stepik.android.exams.data.db.AppDatabase
import org.stepik.android.exams.data.db.dao.NavigationDao
import org.stepik.android.exams.data.db.dao.StepDao
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
    fun providesNavigationDao(app: AppDatabase): NavigationDao {
        return app.navigationDao()
    }
}