/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.interviewtest;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.runner.AndroidJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;
import rx.plugins.RxJavaHooks;

public class EspressoTestRunner extends AndroidJUnitRunner {

    private List<IdlingResource> resources = new ArrayList<>();

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, MockAndroidApplication.class.getName(), context);
    }

    @Override
    public void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        RxJavaHooks.reset();
        RxJavaHooks.setOnComputationScheduler(scheduler -> wrapScheduler(scheduler, "RxJava Computation Scheduler"));
        RxJavaHooks.setOnIOScheduler(scheduler -> wrapScheduler(scheduler, "RxJava I/O Scheduler"));
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> wrapScheduler(scheduler, "RxJava New Thread Scheduler"));
    }

    private Scheduler wrapScheduler(Scheduler scheduler, String resourceName) {
        IdlingResourceScheduler wrapped =
                new IdlingResourceScheduler(scheduler, resourceName);
        resources.add(wrapped.countingIdlingResource());
        return wrapped;
    }

    public void registerIdlingResources() {
        for (IdlingResource resource : resources) {
            Espresso.registerIdlingResources(resource);
        }
    }

    public void unregisterIdlingResources() {
        for (IdlingResource resource : resources) {
            Espresso.unregisterIdlingResources(resource);
        }
        runOnMainSync(() -> resources.clear());
    }
}
