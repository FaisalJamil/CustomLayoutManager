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
 * Use case deletes an {@link Item }.
 */
public class DeleteItem extends UseCase<Item, DeleteItem.Params> {

    private final ItemRepository itemRepository;

    @Inject
    public DeleteItem(ItemRepository itemRepository,
                      @Named("SubscriberThread") ThreadExecutor subscriberThread,
                      @Named("ObserverThread") ThreadExecutor observerThread) {
        super(subscriberThread, observerThread);
        this.itemRepository = itemRepository;
    }

    /**
     * We leave the repository the responsibility of the request, and once we get the deleted item
     * we just return.
     *
     * @param params The Params.
     * @return Observable of the number of Comments
     */
    @Override
    public Observable<Item> provideObservable(Params params) {
        if(params == null) return Observable.error(new NullParameterException(Params.class));
        return this.itemRepository.deleteItem(params.position).toObservable();
    }

    public static class Params {

        private final int position;

        private Params(int position) {

            this.position = position;
        }

        public static Params with(int position) {
            return new Params(position);
        }
    }
}
