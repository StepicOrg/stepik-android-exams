package org.stepik.android.exams.di.network

import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.stepik.android.exams.api.auth.AuthInterceptor
import org.stepik.android.exams.util.setTimeoutsInSeconds
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NetworkHelper {
    const val TIMEOUT_IN_SECONDS = 60L

    @JvmStatic
    fun createRetrofit(client: OkHttpClient, baseUrl: String, gson: Gson = Gson()): Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()


    @JvmStatic
    inline fun <reified T>
            createServiceWithAuth(authInterceptor: AuthInterceptor, host: String, gson: Gson = Gson()): T {
        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder.addInterceptor(authInterceptor)
        okHttpBuilder.setTimeoutsInSeconds(NetworkHelper.TIMEOUT_IN_SECONDS)
        val retrofit = NetworkHelper.createRetrofit(okHttpBuilder.build(), host, gson)

        return retrofit.create(T::class.java)
    }
}