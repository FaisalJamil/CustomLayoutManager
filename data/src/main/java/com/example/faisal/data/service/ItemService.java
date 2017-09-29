/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.data.service;

import com.example.faisal.domain.Item;
import com.example.faisal.domain.ListOfItems;

import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Single;


public interface ItemService {
    String LIST_PICS_PATH = "some/path";
    String ADD_ITEM_PATH = "add/path";
    String DELETE_ITEM_PATH = "delete/path/{position}";
    String MOVE_ITEM_PATH = "move/path";

    @GET(LIST_PICS_PATH)
    Single<ListOfItems> getItems();

    @FormUrlEncoded
    @POST(ADD_ITEM_PATH)
    Single<Item> addItem(@Field("uuid") String uuid,
                         @Field("imageUrlString") String url);

    @DELETE(DELETE_ITEM_PATH)
    Single<Item> deleteItem(@Path("position") int position);

    @FormUrlEncoded
    @POST(MOVE_ITEM_PATH)
    Single<String> moveItem(@Field("from") int fromPosition,
                             @Field("to") int toPosition);
}
