/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.data.repository;

import android.support.annotation.CallSuper;

import okhttp3.mockwebserver.MockWebServer;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

class BaseTestCase {

    MockWebServer server;

    @CallSuper
    public void setUp() throws Exception {
        server = new MockWebServer();
        // Emit immediately
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
    }

    @CallSuper
    public void tearDown() throws Exception {
        server.shutdown();
        RxAndroidPlugins.getInstance().reset();
    }
}
