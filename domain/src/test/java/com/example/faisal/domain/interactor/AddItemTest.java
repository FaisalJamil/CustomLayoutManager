/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.domain.interactor;

import com.example.faisal.domain.Item;
import com.example.faisal.domain.executor.ThreadExecutor;
import com.example.faisal.domain.interactor.base.UseCase;
import com.example.faisal.domain.repository.ItemRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import rx.Single;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Testing AddItem UseCase.
 *
 * We only make sure the business logic of the UseCase is correct. We don't mind on objects outside our scope,
 * which is the Domain layer, thus testing a Repository or a Network request belongs to the Data layer.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddItemTest {

    private AddItem addItemUseCase;

    @Mock private ThreadExecutor mockThreadExecutor;
    @Mock private ItemRepository mockItemRepository;

    @Before
    public void setUp() {
        // AddItem is the class we want to test
        addItemUseCase = new AddItem(mockItemRepository, mockThreadExecutor, mockThreadExecutor);
    }

    /**
     * Checks whether AddItem UseCase is working as expected and the behaviour of the code have not changed.
     */
    @Test
    public void testAddItemObservableCase() {
        // Preconditions
        Item item = mock(Item.class);
        TestSubscriber<Item> testSubscriber = new TestSubscriber<>();
        TestScheduler testScheduler = new TestScheduler();
        given(mockThreadExecutor.getScheduler()).willReturn(testScheduler);
        given(mockItemRepository.addItem(anyString(), anyString())).willReturn(Single.just(item));

        // Attaches the subscriber and executes the Observable.
        Subscription subscription = addItemUseCase.execute(testSubscriber, mock(AddItem.Params.class));
        testScheduler.triggerActions();
        subscription.unsubscribe();

        // Checks whether these methods from inner objects are called.
        verify(mockItemRepository).addItem(anyString(), anyString());
        verify(mockThreadExecutor, times(2)).getScheduler();

        // Ensures there aren't new changes on the Repository calls and UseCase structure
        verifyNoMoreInteractions(mockItemRepository, mockThreadExecutor);

        // Ensures Observer is emitting only the mocked element we passed through before
        testSubscriber.assertValueCount(1);
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValue(item);

        // Ensures the Subscriber is not subscribed anymore
        testSubscriber.assertUnsubscribed();
    }

    /**
     * Checks it carry on an Exception if AddItem.Params is not provided through the {@link rx.Subscriber#onError(Throwable)} method.
     */
    @Test
    public void testAddItemWithNoParamsThrowsException() {
        // Preconditions
        TestSubscriber<Item> testSubscriber = new TestSubscriber<>();
        TestScheduler testScheduler = new TestScheduler();
        given(mockThreadExecutor.getScheduler()).willReturn(testScheduler);

        // Attaches the subscriber and executes the Observable.
        Subscription subscription = addItemUseCase.execute(testSubscriber);
        testScheduler.triggerActions();
        subscription.unsubscribe();

        // Checks whether these methods from inner objects are NEVER called.
        verify(mockItemRepository, never()).addItem(anyString(), anyString());
        verify(mockThreadExecutor, times(2)).getScheduler();

        // Ensures there aren't new changes on the Repository calls and UseCase structure
        verifyNoMoreInteractions(mockItemRepository, mockThreadExecutor);

        // Ensures Observer is emitting NullPointerException in the onError method
        testSubscriber.assertError(UseCase.NullParameterException.class);
        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();

        List<Throwable> throwableList = testSubscriber.getOnErrorEvents();
        Assert.assertTrue(throwableList.size() > 0);
        //noinspection ThrowableResultOfMethodCallIgnored
        Assert.assertEquals(String.format(UseCase.NULL_PARAMETER, AddItem.Params.class.getSimpleName()),
                throwableList.get(0).getMessage());

        // Ensures the Subscriber is not subscribed anymore
        testSubscriber.assertUnsubscribed();
    }
}