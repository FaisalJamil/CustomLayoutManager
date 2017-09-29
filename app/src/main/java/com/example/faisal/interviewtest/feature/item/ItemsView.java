/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.interviewtest.feature.item;

import com.example.faisal.domain.Item;
import com.example.faisal.domain.ListOfItems;
import com.example.faisal.interviewtest.internal.mvp.contract.Presentable;
import com.example.faisal.interviewtest.internal.mvp.contract.Viewable;

public interface ItemsView<T extends Presentable> extends Viewable<T> {

    void render(ListOfItems itemsList);
    void add(Item data);
    void delete (int position);
}
