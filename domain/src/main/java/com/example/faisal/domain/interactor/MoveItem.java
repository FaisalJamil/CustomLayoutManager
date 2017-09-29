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
 * Use case moves and swaps {@link Item }s.
 */
public class MoveItem extends UseCase<String, MoveItem.Params> {

    private final ItemRepository itemRepository;

    @Inject
    public MoveItem(ItemRepository itemRepository,
                    @Named("SubscriberThread") ThreadExecutor subscriberThread,
                    @Named("ObserverThread") ThreadExecutor observerThread) {
        super(subscriberThread, observerThread);
        this.itemRepository = itemRepository;
    }

    /**
     * We leave the repository the responsibility of the request, and once we get the move message
     * we just return.
     *
     * @param params The Params.
     * @return Observable of the number of Comments
     */
    @Override
    public Observable<String> provideObservable(Params params) {
        if(params == null) return Observable.error(new NullParameterException(Params.class));
        return this.itemRepository.moveItem(params.fromPosition, params.toPosition).toObservable();
    }

    public static class Params {

        private final int fromPosition;
        private final int toPosition;

        private Params(int fromPosition, int toPosition) {

            this.fromPosition = fromPosition;
            this.toPosition = toPosition;
        }

        public static Params with(int fromPosition, int toPosition) {
            return new Params(fromPosition, toPosition);
        }
    }
}
