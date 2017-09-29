/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.domain.executor;

import rx.Scheduler;

/**
 * Default Executor interface that provides its Scheduler.
 *
 * This is a contract which establishes the {@link Scheduler} that must be applied to an {@link rx.Observable}
 * either on its {@link rx.Observable#observeOn(Scheduler)} or {@link rx.Observable#subscribeOn(Scheduler)} method.
 */
public interface ThreadExecutor {

    /**
     * Provides a specific {@link Scheduler}.
     *
     * @return The Scheduler
     */
    Scheduler getScheduler();
}