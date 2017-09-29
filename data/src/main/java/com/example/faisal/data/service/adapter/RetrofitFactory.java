/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.data.service.adapter;

import android.content.Context;

import com.example.faisal.data.MockClient;
import com.google.gson.GsonBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Simple implementation of a RestFactory class based on Retrofit 2
 */
@Singleton
public class RetrofitFactory implements RestFactory {

    private static GsonBuilder gsonBuilder = new GsonBuilder();

    private String apiUrl;

    @Inject
    public RetrofitFactory() {}

    @Override
    public <S> S create(Class<S> service) {
        return createBuilder()
                .baseUrl(this.apiUrl)
                .client(getClient())
                .build()
                .create(service);
    }

    @Override
    public OkHttpClient getClient() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        //TODO: comment this line before running tests in data
        clientBuilder.addInterceptor(new MockClient());
        clientBuilder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        return clientBuilder.build();
    }

    private Retrofit.Builder createBuilder() {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
    }

    @Override
    public void setBaseUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
