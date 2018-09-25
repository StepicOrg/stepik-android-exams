package org.stepik.android.exams.di


import android.content.Context
import android.content.pm.PackageManager
import android.webkit.CookieManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.stepik.android.exams.configuration.Config
import org.stepik.android.exams.configuration.ConfigImpl
import org.stepik.android.exams.core.ScreenManager
import org.stepik.android.exams.core.ScreenManagerImpl
import org.stepik.android.exams.core.interactor.GraphInteractorImpl
import org.stepik.android.exams.core.interactor.ProgressInteractorImpl
import org.stepik.android.exams.core.interactor.contacts.GraphInteractor
import org.stepik.android.exams.core.interactor.contacts.ProgressInteractor
import org.stepik.android.exams.data.preference.ProfilePreferences
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.util.AppConstants
import org.stepik.android.exams.util.resolvers.text.TextResolver
import org.stepik.android.exams.util.resolvers.text.TextResolverImpl
import javax.inject.Named

@Module
abstract class AppCoreModule {

    @AppSingleton
    @Binds
    abstract fun provideGraphInteractor(graphInteractorImpl: GraphInteractorImpl): GraphInteractor

    @AppSingleton
    @Binds
    abstract fun provideProgressInteractor(progressInteractorImpl: ProgressInteractorImpl): ProgressInteractor

    @Binds
    @AppSingleton
    abstract fun provideAuthRepository(sharedPreferenceHelper: SharedPreferenceHelper): ProfilePreferences

    @Binds
    @AppSingleton
    abstract fun provideScreenManager(screenManagerImpl: ScreenManagerImpl): ScreenManager

    @Binds
    @AppSingleton
    internal abstract fun provideTextResolver(textResolver: TextResolverImpl): TextResolver

    @Module
    companion object {
        @Provides
        @JvmStatic
        @MainScheduler
        internal fun provideAndroidScheduler(): Scheduler = AndroidSchedulers.mainThread()

        @Provides
        @JvmStatic
        @BackgroundScheduler
        internal fun provideBackgroundScheduler(): Scheduler = Schedulers.io()

        @Provides
        @AppSingleton
        @JvmStatic
        internal fun provideConfig(configFactory: ConfigImpl.ConfigFactory): Config =
                configFactory.create()

        @JvmStatic
        @Provides
        @AppSingleton
        @Named(AppConstants.userAgentName)
        internal fun provideUserAgent(context: Context): String =
                try {
                    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                    val apiLevel = android.os.Build.VERSION.SDK_INT
                    ("StepikDroid/" + packageInfo.versionName + " (Android " + apiLevel
                            + ") build/" + packageInfo.versionCode + " package/" + packageInfo.packageName)
                } catch (e: PackageManager.NameNotFoundException) {
                    ""
                }


        @JvmStatic
        @Provides
        @AppSingleton
        internal fun provideCookieManager(): CookieManager = CookieManager.getInstance()

    }
}