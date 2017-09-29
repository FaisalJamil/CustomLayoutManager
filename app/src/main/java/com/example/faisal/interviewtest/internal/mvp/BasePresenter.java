/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.interviewtest.internal.mvp;

import android.support.annotation.NonNull;

import com.example.faisal.domain.interactor.base.UseCase;
import com.example.faisal.interviewtest.internal.mvp.contract.Presentable;
import com.example.faisal.interviewtest.internal.mvp.contract.Viewable;

public class BasePresenter<T extends Viewable> implements Presentable<T> {

    private T viewable;

    protected void attachLoading(UseCase... useCases) {
        for (UseCase useCase : useCases) {
            useCase.onSubscribe(() -> getView().showLoading());
            useCase.onTerminate(() -> getView().hideLoading());
        }
    }

    @Override
    public void onStart() {
        // No-op by default
    }

    @Override
    public void onViewCreated() {
        // No-op by default
    }

    @Override
    public void onResume() {
        // No-op by default
    }

    @Override
    public void onPause() {
        // No-op by default
    }

    @Override
    public void onStop() {
        // No-op by default
    }

    @Override
    public void attachView(@NonNull T viewable) {
        this.viewable = viewable;
    }

    @Override
    public void detachView() {
        this.viewable = null;
    }

    @Override
    public T getView() {
        return viewable;
    }
}
