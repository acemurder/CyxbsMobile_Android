package com.redrock.common.network;

import com.redrock.common.BuildConfig;
import com.redrock.common.config.Const;
import com.redrock.common.network.interceptor.StudentNumberInterceptor;
import com.redrock.common.network.setting.QualifiedTypeConverterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RequestProvider {
    Retrofit retrofit;

    public RequestProvider() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Const.END_POINT_RED_ROCK)
                .client(configureOkHttp(new OkHttpClient.Builder()))
                .addConverterFactory(new QualifiedTypeConverterFactory(
                        GsonConverterFactory.create(), SimpleXmlConverterFactory.create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    private OkHttpClient configureOkHttp(OkHttpClient.Builder builder) {
        builder.connectTimeout(30L, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }
        builder.addInterceptor(new StudentNumberInterceptor());
        return builder.build();
    }
}
