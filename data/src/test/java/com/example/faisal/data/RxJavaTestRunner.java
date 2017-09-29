/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.data;

import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;

import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

public class RxJavaTestRunner extends MockitoJUnitRunner {

    public RxJavaTestRunner(Class<?> testClass) throws InvocationTargetException {
        super(testClass);
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.immediate());
    }

}
