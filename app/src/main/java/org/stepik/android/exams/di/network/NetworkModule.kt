package org.stepik.android.exams.di.network

import org.stepik.android.exams.api.graph.BASE_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.stepik.android.exams.api.auth.EmptyAuthService
import org.stepik.android.exams.api.graph.GraphService
import org.stepik.android.exams.data.preference.AuthPreferences
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.di.AppSingleton
import org.stepik.android.exams.util.setTimeoutsInSeconds

@Module(includes = [AuthModule::class, ProfileModule::class])
abstract class NetworkModule {

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
        @Provides
        @AppSingleton
        @JvmStatic
        internal fun provideGraphService() : GraphService{
            val okHttpBuilder = OkHttpClient.Builder()
            okHttpBuilder.setTimeoutsInSeconds(NetworkHelper.TIMEOUT_IN_SECONDS)
            val retrofit = NetworkHelper.createRetrofit(okHttpBuilder.build(), BASE_URL)
            return retrofit.create(GraphService::class.java)
        }
    }

}
