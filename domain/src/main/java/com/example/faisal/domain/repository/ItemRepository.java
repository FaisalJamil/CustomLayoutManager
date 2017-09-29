/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.domain.repository;

import com.example.faisal.domain.Item;
import com.example.faisal.domain.ListOfItems;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.List;

import rx.Completable;
import rx.Single;


public interface ItemRepository {
    /**
     * Gets a {@link Single} which will emit a List of {@link Item}s.
     *
     * @return Single List
     */
    Single<ListOfItems> getAllItems();
    Single<Item> addItem(String uuid, String url);
    Single<Item> deleteItem(int position);
    Single<String> moveItem(int fromPosition, int toPosition);
}
