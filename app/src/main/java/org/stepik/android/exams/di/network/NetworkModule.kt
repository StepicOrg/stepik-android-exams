package org.stepik.android.exams.di.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import org.stepik.android.exams.data.model.DatasetWrapper
import org.stepik.android.exams.data.preference.AuthPreferences
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.di.AppSingleton

@Module(includes = [AuthModule::class, ProfileModule::class])
abstract class NetworkModule {

    // AuthModule link
    @Binds
    @AppSingleton
    abstract fun provideAuthPreferences(sharedPreferenceHelper: SharedPreferenceHelper): AuthPreferences

    @Module
    companion object {
        @Provides
        @AppSingleton
        @JvmStatic
        internal fun provideModelGson(): Gson = GsonBuilder()
                .create()

/*        @Provides
        @AppSingleton
        @JvmStatic
        internal fun provideStepikService(authInterceptor: AuthInterceptor, config: Config, gson: Gson): StepikService =
                NetworkHelper.createServiceWithAuth(authInterceptor, config.host, gson)
    }*/

    }
}