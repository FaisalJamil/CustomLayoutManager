/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.interviewtest.internal.mvp.contract;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Android contract for every MVP View
 */
public interface Viewable<T> {

    /**
     * Sets the Title of the Screen
     */
    void setTitle(@StringRes int resource);

    /**
     * Sets the Title of the Screen
     */
    void setTitle(@NonNull CharSequence msg);

    /**
     * Every Viewable must be able to access to its attached Presenter
     *
     * @return Presentable
     */
    T getPresenter();

    /**
     * Every Viewable must be able to inject its Presenter
     *
     * @param presenter Presentable
     */
    void injectPresenter(T presenter);

    /**
     * Every Viewable must have a error message system
     */
    void displayError(String message);

    /**
     * Every Viewable must have a error message system
     */
    void displayError(int messageId);

    /**
     * Every Viewable must implement one show loading feature
     */
    void showLoading();

    /**
     * Every Viewable must implement one hide loading feature
     */
    void hideLoading();

}
