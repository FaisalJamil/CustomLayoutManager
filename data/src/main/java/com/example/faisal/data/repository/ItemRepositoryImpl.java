/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.data.repository;

import android.content.Context;

import com.example.faisal.data.service.ItemService;
import com.example.faisal.data.service.adapter.RestFactory;
import com.example.faisal.domain.Item;
import com.example.faisal.domain.ListOfItems;
import com.example.faisal.domain.repository.ItemRepository;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Single;


public class ItemRepositoryImpl implements ItemRepository {
    private final RestFactory api;
    private final String apiUrl;

    @Inject
    ItemRepositoryImpl(RestFactory api, @Named("ApiUrl") String apiUrl) {
        this.api = api;
        this.apiUrl = apiUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Single<ListOfItems> getAllItems() {
        this.api.setBaseUrl(apiUrl); // Ability to change the API Url at any time.
        return this.api.create(ItemService.class).getItems();
    }

    @Override
    public Single<Item> addItem(String uuid, String url) {
        this.api.setBaseUrl(apiUrl);
        return this.api.create(ItemService.class).addItem(uuid, url);
    }

    @Override
    public Single<Item> deleteItem(int position) {
        this.api.setBaseUrl(apiUrl);
        return this.api.create(ItemService.class).deleteItem(position);
    }

    @Override
    public Single<String> moveItem(int fromPosition, int toPosition) {
        this.api.setBaseUrl(apiUrl);
        return this.api.create(ItemService.class).moveItem(fromPosition, toPosition);
    }
}
