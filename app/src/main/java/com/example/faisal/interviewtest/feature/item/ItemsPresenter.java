/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.interviewtest.feature.item;

import com.example.faisal.domain.Item;
import com.example.faisal.domain.ListOfItems;
import com.example.faisal.domain.interactor.AddItem;
import com.example.faisal.domain.interactor.DeleteItem;
import com.example.faisal.domain.interactor.GetItemList;
import com.example.faisal.domain.interactor.MoveItem;
import com.example.faisal.interviewtest.R;
import com.example.faisal.interviewtest.internal.di.PerFragment;
import com.example.faisal.interviewtest.internal.mvp.BasePresenter;

import javax.inject.Inject;

import rx.SingleSubscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

@PerFragment
public class ItemsPresenter extends BasePresenter<ItemsView> {

    private GetItemList getItemListUseCase;
    private AddItem addItemUseCase;
    private DeleteItem deleteItemUseCase;
    private MoveItem moveItemUseCase;

    CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    public ItemsPresenter(GetItemList getItemListUseCase, AddItem addItemUseCase,
                          DeleteItem deleteItemUseCase, MoveItem moveItemUseCase) {
        this.getItemListUseCase = getItemListUseCase;
        attachLoading(this.getItemListUseCase);
        this.addItemUseCase = addItemUseCase;
        attachLoading(this.addItemUseCase);
        this.deleteItemUseCase = deleteItemUseCase;
        attachLoading(this.deleteItemUseCase);
        this.moveItemUseCase = moveItemUseCase;
    }

    @Override
    public void onViewCreated() {
        Subscription subscription = getItemListUseCase.execute(new SingleSubscriber<ListOfItems>() {
            @Override
            public void onSuccess(ListOfItems data) {
                getView().render(data);
            }

            @Override
            public void onError(Throwable error) {
                getView().displayError(R.string.common_error);
            }
        });

        compositeSubscription.add(subscription);
    }

    public void addItem(String uuid, String url) {
        Subscription subscription = addItemUseCase.execute(new SingleSubscriber<Item>() {
            @Override
            public void onSuccess(Item data) {
                getView().add(data);
            }

            @Override
            public void onError(Throwable error) {
                getView().displayError(R.string.common_error);
            }
        },AddItem.Params.with(uuid, url));

        compositeSubscription.add(subscription);
    }


    public void deleteItem(int position) {
        Subscription subscription = deleteItemUseCase.execute(new SingleSubscriber<Item>() {
            @Override
            public void onSuccess(Item data) {
                getView().delete(position);
            }

            @Override
            public void onError(Throwable error) {
                getView().displayError(R.string.common_error);
            }
        }, DeleteItem.Params.with(position));

        compositeSubscription.add(subscription);
    }

    public void moveItem(int fromPosition, int toPosition) {
        Subscription subscription = moveItemUseCase.execute(new SingleSubscriber<String>() {
            @Override
            public void onSuccess(String data) {
                getView().displayError(R.string.item_swapped);
            }

            @Override
            public void onError(Throwable error) {
                getView().displayError(R.string.common_error);
            }
        }, MoveItem.Params.with(fromPosition, toPosition));

        compositeSubscription.add(subscription);
    }
    @Override
    public void onStop() {
        compositeSubscription.clear();
        super.onStop();
    }


}
