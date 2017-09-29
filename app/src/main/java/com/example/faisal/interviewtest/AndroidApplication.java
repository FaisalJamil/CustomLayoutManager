/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.interviewtest;

import android.app.Application;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.example.faisal.data.service.adapter.RetrofitFactory;
import com.example.faisal.interviewtest.internal.di.components.ApplicationComponent;
import com.example.faisal.interviewtest.internal.di.components.DaggerApplicationComponent;
import com.example.faisal.interviewtest.internal.di.modules.ApplicationModule;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;

public class AndroidApplication extends Application {

    @VisibleForTesting(otherwise = MODE_PRIVATE)
    protected ApplicationComponent applicationComponent;
    private RetrofitFactory factory;

    @Override public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
        initializeInjector();
        Fresco.initialize(this);
    }

    @VisibleForTesting(otherwise = MODE_PRIVATE)
    protected void initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }

    public static ApplicationComponent getComponent(Context context) {
        return ((AndroidApplication)context.getApplicationContext()).getApplicationComponent();
    }
}
