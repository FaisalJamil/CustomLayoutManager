/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.domain.interactor;

import com.example.faisal.domain.ListOfItems;
import com.example.faisal.domain.executor.ThreadExecutor;
import com.example.faisal.domain.repository.ItemRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Single;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Testing GetItemList UseCase.
 *
 * We  make sure the business logic of the UseCase is correct. We don't mind on objects outside our scope,
 * which is the Domain layer, thus testing a Repository or a Network request belongs to the Data layer.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetItemListTest {

    private GetItemList getItemListUseCase;

    @Mock private ThreadExecutor mockThreadExecutor;
    @Mock private ItemRepository mockItemRepository;

    @Before
    public void setUp() {
        // GetItemList is the class we want to test
        getItemListUseCase = new GetItemList(mockItemRepository,
                mockThreadExecutor, mockThreadExecutor);
    }

    /**
     * Checks whether GetItemList UseCase is working as expected and the behaviour of the code have not changed.
     */
    @Test
    public void testGetPostListObservableCase() {
        // Preconditions
        ListOfItems itemList = mock(ListOfItems.class);
        TestSubscriber<ListOfItems> testSubscriber = new TestSubscriber<>();
        TestScheduler testScheduler = new TestScheduler();
        given(mockThreadExecutor.getScheduler()).willReturn(testScheduler);
        given(mockItemRepository.getAllItems()).willReturn(Single.just(itemList));

        // Attaches the subscriber and executes the Observable.
        Subscription subscription = getItemListUseCase.execute(testSubscriber);
        testScheduler.triggerActions();
        subscription.unsubscribe();

        // Checks whether these methods from inner objects are called.
        verify(mockItemRepository).getAllItems();
        verify(mockThreadExecutor, times(2)).getScheduler();

        // Ensures there aren't new changes on the Repository calls and UseCase structure
        verifyNoMoreInteractions(mockItemRepository, mockThreadExecutor);

        // Ensures Observer is emitting only the mocked element we passed through before
        testSubscriber.assertValueCount(1);
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValue(itemList);

        // Ensures the Subscriber is not subscribed anymore
        testSubscriber.assertUnsubscribed();
    }
}
