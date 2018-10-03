package org.stepik.android.exams.di.modules

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.stepik.android.exams.api.auth.AuthInterceptor
import org.stepik.android.exams.api.profile.ProfileRepository
import org.stepik.android.exams.api.profile.ProfileRepositoryImpl
import org.stepik.android.exams.api.profile.ProfileService
import org.stepik.android.exams.configuration.Config
import org.stepik.android.exams.di.AppSingleton

@Module
abstract class ProfileModule {
    @Binds
    @AppSingleton
    abstract fun provideAuthRepository(profileRepositoryImpl: ProfileRepositoryImpl): ProfileRepository

    @Module
    companion object {
        @Provides
        @AppSingleton
        @JvmStatic
        internal fun provideProfileService(authInterceptor: AuthInterceptor, config: Config): ProfileService =
                NetworkHelper.createServiceWithAuth(authInterceptor, config.host)
    }
}