/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.interviewtest;

import com.example.faisal.interviewtest.internal.di.components.ApplicationComponent;

public class MockAndroidApplication extends AndroidApplication {

    @Override
    protected void initializeInjector() {
        // override in order to clear out default implementation
    }

    public void setComponent(ApplicationComponent component) {
        this.applicationComponent = component;
    }

}
