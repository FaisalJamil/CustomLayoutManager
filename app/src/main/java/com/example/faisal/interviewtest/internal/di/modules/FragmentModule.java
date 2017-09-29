/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.interviewtest.internal.di.modules;


import android.support.v4.app.Fragment;

import com.example.faisal.interviewtest.internal.di.PerFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentModule {
    private final Fragment fragment;

    public FragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    @PerFragment
    Fragment fragment() {
        return this.fragment;
    }
}
