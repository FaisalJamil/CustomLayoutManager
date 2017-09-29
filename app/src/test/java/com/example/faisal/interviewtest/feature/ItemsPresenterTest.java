/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.interviewtest.feature;

import com.example.faisal.domain.ListOfItems;
import com.example.faisal.domain.executor.ThreadExecutor;
import com.example.faisal.domain.interactor.AddItem;
import com.example.faisal.domain.interactor.DeleteItem;
import com.example.faisal.domain.interactor.GetItemList;
import com.example.faisal.domain.interactor.MoveItem;
import com.example.faisal.domain.repository.ItemRepository;
import com.example.faisal.interviewtest.feature.item.ItemsPresenter;
import com.example.faisal.interviewtest.feature.item.ItemsView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;
import rx.SingleSubscriber;
import rx.schedulers.TestScheduler;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Testing ItemsPresenter.
 *
 * As a unit test, we only focus on testing the behaviour of the Presenter is working as expected.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemsPresenterTest extends BaseUnitTestCase {

    @Mock private ThreadExecutor mockThreadExecutor;
    @Mock private ItemRepository mockItemRepository;

    private GetItemList     itemListModel;
    private AddItem         addItemModel;
    private DeleteItem      deleteItemModel;
    private MoveItem        moveItemModel;
    private ItemsView       view;
    private ItemsPresenter  presenter;

    @Before
    public void setup() throws Exception {
        super.setUp();
        view = mock(ItemsView.class);
        itemListModel = spy(new GetItemList(mockItemRepository, mockThreadExecutor, mockThreadExecutor));
        addItemModel = spy(new AddItem(mockItemRepository, mockThreadExecutor, mockThreadExecutor));
        deleteItemModel = spy(new DeleteItem(mockItemRepository, mockThreadExecutor, mockThreadExecutor));
        moveItemModel = spy(new MoveItem(mockItemRepository, mockThreadExecutor, mockThreadExecutor));

        // ItemsPresenter is the class we want to test
        presenter = new ItemsPresenter(itemListModel, addItemModel, deleteItemModel, moveItemModel);
        presenter.attachView(view);

        //noinspection unchecked
        doCallRealMethod().when(itemListModel).execute(any(SingleSubscriber.class));
        doCallRealMethod().when(addItemModel).execute(any(SingleSubscriber.class));
        doCallRealMethod().when(deleteItemModel).execute(any(SingleSubscriber.class));
        doCallRealMethod().when(moveItemModel).execute(any(SingleSubscriber.class));
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Checks that either the Model and the View are fired when the Presenter starts its execution.
     */
    @Test
    public void testPresenterOk() {
        // Preconditions
        ListOfItems itemList = mock(ListOfItems.class);
        TestScheduler testScheduler = new TestScheduler();
        given(mockThreadExecutor.getScheduler()).willReturn(testScheduler);
        doReturn(Observable.just(itemList)).when(itemListModel).provideObservable(any(Void.class));

        // Simulates the onViewCreated method is called from the BaseFragment class
        presenter.onViewCreated();
        testScheduler.triggerActions();

        // Checks model and view methods are fired respectively
        //noinspection unchecked
        verify(itemListModel).execute(any(SingleSubscriber.class));
        verify(itemListModel).asObservable(any(Void.class));
        verify(view).showLoading();
        verify(view).hideLoading();
        verify(view).render(itemList);
    }
}
