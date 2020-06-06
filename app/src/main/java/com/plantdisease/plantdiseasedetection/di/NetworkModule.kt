package com.plantdisease.plantdiseasedetection.di

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.plantdisease.plantdiseasedetection.BuildConfig
import com.plantdisease.plantdiseasedetection.Config
import com.plantdisease.plantdiseasedetection.network.PredictAPI
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal val networkModule = module {
    single { GsonBuilder().create() as Gson }
    single { provideOkHttpProxy(get()) }
    single { provideApi(get(), get()) }
}

private fun provideOkHttpProxy(
    application: Application
): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.setLevel(
        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    )
    val cache = Cache(application.cacheDir, 1024 * 1024 * 10)
    val builder: OkHttpClient.Builder = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .cache(cache)

    return builder.build()
}

fun provideApi(client: OkHttpClient, gson: Gson): PredictAPI {
    val retrofit: Retrofit = Retrofit.Builder().addCallAdapterFactory(
        RxJava2CallAdapterFactory.create()
    )
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .baseUrl(Config.BASE_ACCOUNT_URL)
        .build()
    return retrofit.create<PredictAPI>(PredictAPI::class.java)
}
