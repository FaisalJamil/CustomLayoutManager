/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.domain.interactor;

import com.example.faisal.domain.Item;
import com.example.faisal.domain.ListOfItems;
import com.example.faisal.domain.executor.ThreadExecutor;
import com.example.faisal.domain.interactor.base.UseCase;
import com.example.faisal.domain.repository.ItemRepository;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

/**
 * Use case that retrieves a List of {@link Item}s.
 */
public class GetItemList extends UseCase<ListOfItems, Void> {

    private final ItemRepository itemRepository;

    @Inject
    public GetItemList(ItemRepository itemRepository,
                       @Named("SubscriberThread") ThreadExecutor subscriberThread,
                       @Named("ObserverThread") ThreadExecutor observerThread) {
        super(subscriberThread, observerThread);
        this.itemRepository = itemRepository;
    }

    /**
     * We make a call to the Items endpoint, and we get the list of all items,
     *
     * @param aVoid Void
     * @return Observable of Posts
     */
    @Override
    public Observable<ListOfItems> provideObservable(Void aVoid) {
        return itemRepository.getAllItems().toObservable();
    }

}