/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.interviewtest.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Utility used to navigate through the App.
 */
@Singleton
public class Navigator {

    @Inject
    public Navigator() {
        // empty
    }

    public void replaceFragment(FragmentActivity activity, int containerViewId, Fragment fragment) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment);
        if (fragmentManager.getFragments() != null) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commitAllowingStateLoss();
        //commit();
    }
}
