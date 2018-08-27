package org.stepik.android.exams.di.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.stepik.android.exams.api.auth.AuthInterceptor
import org.stepik.android.exams.jsonHelpers.DatasetDeserializer
import org.stepik.android.exams.jsonHelpers.ReplyDeserializer
import org.stepik.android.exams.jsonHelpers.ReplySerializer
import org.stepik.android.exams.jsonHelpers.adapters.CodeOptionsAdapterFactory
import org.stepik.android.exams.util.setTimeoutsInSeconds
import org.stepik.android.model.ReplyWrapper
import org.stepik.android.model.attempts.DatasetWrapper
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NetworkHelper {
    const val TIMEOUT_IN_SECONDS = 60L

    @JvmStatic
    fun createRetrofit(client: OkHttpClient, baseUrl: String, gson: Gson = Gson()): Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(generateGsonFactory())
            .client(client)
            .build()

    @JvmStatic
    private fun generateGsonFactory(): Converter.Factory {
        val gson = GsonBuilder()
                .enableComplexMapKeySerialization()
                .registerTypeAdapterFactory(CodeOptionsAdapterFactory())
                .registerTypeAdapter(DatasetWrapper::class.java, DatasetDeserializer())
                .registerTypeAdapter(ReplyWrapper::class.java, ReplyDeserializer())
                .registerTypeAdapter(ReplyWrapper::class.java, ReplySerializer())
                .create()
        return GsonConverterFactory.create(gson)
    }


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