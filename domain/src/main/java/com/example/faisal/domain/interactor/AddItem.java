/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.domain.interactor;

import com.example.faisal.domain.Item;
import com.example.faisal.domain.executor.ThreadExecutor;
import com.example.faisal.domain.interactor.base.UseCase;
import com.example.faisal.domain.repository.ItemRepository;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

/**
 * Use case adds an {@link Item }.
 */
public class AddItem extends UseCase<Item, AddItem.Params> {

    private final ItemRepository itemRepository;

    @Inject
    public AddItem(ItemRepository itemRepository,
                   @Named("SubscriberThread") ThreadExecutor subscriberThread,
                   @Named("ObserverThread") ThreadExecutor observerThread) {
        super(subscriberThread, observerThread);
        this.itemRepository = itemRepository;
    }

    /**
     * We leave the repository the responsibility of the request, and once we get the added item
     * we just return.
     *
     * @param params The Params.
     * @return Observable of the number of Comments
     */
    @Override
    public Observable<Item> provideObservable(Params params) {
        if(params == null) return Observable.error(new NullParameterException(Params.class));
        return this.itemRepository.addItem(params.uuid, params.url).toObservable();
    }

    public static class Params {

        private final String uuid;
        private final String url;

        private Params(String uuid, String url) {

            this.uuid = uuid;
            this.url = url;
        }

        public static Params with(String uuid, String url) {
            return new Params(uuid, url);
        }
    }
}
