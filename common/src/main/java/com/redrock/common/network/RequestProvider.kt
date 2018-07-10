package com.redrock.common.network

import com.redrock.common.BuildConfig
import com.redrock.common.config.Const
import com.redrock.common.network.interceptor.StudentNumberInterceptor
import com.redrock.common.network.setting.QualifiedTypeConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

object RequestProvider {

    var retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(Const.END_POINT_RED_ROCK)
                .client(configureOkHttp(OkHttpClient.Builder()))
                .addConverterFactory(QualifiedTypeConverterFactory(
                        GsonConverterFactory.create(), SimpleXmlConverterFactory.create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()!!
    }

    private fun configureOkHttp(builder: OkHttpClient.Builder): OkHttpClient {
        builder.connectTimeout(30L, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(logging)
        }
        builder.addInterceptor(StudentNumberInterceptor())
        return builder.build()
    }
}