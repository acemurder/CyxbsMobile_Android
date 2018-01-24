package com.mredrock.cyxbs.common.net

import com.mredrock.cyxbs.common.config.Const
import com.mredrock.cyxbs.common.data.DataBus
import com.mredrock.cyxbs.common.net.interceptor.StudentNumberInterceptor
import com.mredrock.cyxbs.common.net.setting.QualifiedTypeConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by ：AceMurder
 * Created on ：2018/1/24
 * Created for : CyxbsMobile_Android.
 * Enjoy it !!!
 */
object ApiGenerator {
    private var defaultRetrofit: Retrofit? = null
    private var okHttpClient: OkHttpClient? = null
    private val DEFAULT_TIMEOUT = 30


    init {
        okHttpClient = configureOkHttp(OkHttpClient.Builder())
        defaultRetrofit = Retrofit.Builder()
                .baseUrl(Const.END_POINT_REDROCK)
                .client(okHttpClient)
                .addConverterFactory(QualifiedTypeConverterFactory(GsonConverterFactory.create(), SimpleXmlConverterFactory.create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }


    private fun configureOkHttp(builder: OkHttpClient.Builder): OkHttpClient {
        builder.connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)

        if (DataBus.isDebug()) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(logging)
        }
        builder.addInterceptor(StudentNumberInterceptor())

        return builder.build()
    }

    fun <T> getApiService(clazz: Class<T>): T {
        return defaultRetrofit!!.create(clazz)
    }

    fun <T> getApiService(retrofit: Retrofit, clazz: Class<T>): T {
        return retrofit.create(clazz)
    }
}