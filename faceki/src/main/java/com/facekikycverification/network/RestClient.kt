package com.facekikycverification.network

import com.facekikycverification.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RestClient {
    var BASE_URL = "https://app.faceki.com/"
    var apiRestInterfaces: IRestInterfaces? = null
    @JvmStatic
    val client: IRestInterfaces?
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(1000, TimeUnit.SECONDS)
                .connectTimeout(1000, TimeUnit.SECONDS)
            if (BuildConfig.DEBUG) okHttpClient.addInterceptor(interceptor)
            if (apiRestInterfaces == null) {
                val client = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient.build())
                    .build()
                apiRestInterfaces =
                    client.create(IRestInterfaces::class.java)
            }
            return apiRestInterfaces
        }
}