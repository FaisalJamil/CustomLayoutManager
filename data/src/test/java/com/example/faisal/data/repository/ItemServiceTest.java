/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.data.repository;

import com.example.faisal.data.MockResponseDispatcher;
import com.example.faisal.data.RxJavaTestRunner;
import com.example.faisal.data.service.ItemService;
import com.example.faisal.data.service.adapter.RetrofitFactory;
import com.example.faisal.domain.Item;
import com.example.faisal.domain.ListOfItems;
import com.example.faisal.domain.repository.ItemRepository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;

import java.util.List;

import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.HttpException;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Testing Item repository and service.
 *
 * As part of the Data layer, we will test that the behaviour of Repositories and Responses
 * from Service requests are working as expected.
 */
@RunWith(RxJavaTestRunner.class)
public class ItemServiceTest extends BaseTestCase {

    @Spy RetrofitFactory retrofitFactory;

    private ItemRepository repository;
    private String storageDir;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockResponseDispatcher.reset();
        server.setDispatcher(MockResponseDispatcher.DISPATCHER);
        server.start();
        repository = new ItemRepositoryImpl(retrofitFactory, server.url("").url().toString());
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Checks whether the Item repository and service is working as expected and the behaviour of the code have not changed.
     */
    @Test
    public void testRepositoryGotResponseOkForItems() throws InterruptedException {
        // Preconditions
        TestSubscriber<ListOfItems> subscriber = new TestSubscriber<>();

        // Attaches the subscriber and executes the Observable.
        repository.getAllItems().subscribe(subscriber);
        RecordedRequest request = server.takeRequest();

        // Checks whether these methods from inner objects are called.
        verify(retrofitFactory).setBaseUrl(anyString());
        verify(retrofitFactory).create(ItemService.class);

        // Since we are using a mockclient temporarily, once we get rid of it we can test getClient()
        verify(retrofitFactory).getClient();

        // Ensures there aren't new changes on the Rest adapter structure
        verifyNoMoreInteractions(retrofitFactory);

        // Ensures Observer and Requests are working as expected
        List<ListOfItems> events = subscriber.getOnNextEvents();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertCompleted();
        subscriber.assertUnsubscribed();
        Assert.assertEquals(1, server.getRequestCount());
        Assert.assertEquals("/some/path", request.getPath());
        Assert.assertNotNull(events);
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(4, events.get(0).items.size());
    }

    /**
     * Checks whether the Item repository and service is working as expected and the behaviour of the code have not changed.
     */
    @Test
    public void testRepositoryGotResponseOkForAddItem() throws InterruptedException {
        // Preconditions
        TestSubscriber<Item> subscriber = new TestSubscriber<>();

        // Attaches the subscriber and executes the Observable.
        repository.addItem("abc", anyString()).subscribe(subscriber);
        RecordedRequest request = server.takeRequest();

        // Checks whether these methods from inner objects are called.
        verify(retrofitFactory).setBaseUrl(anyString());
        verify(retrofitFactory).create(ItemService.class);
        verify(retrofitFactory).getClient();

        // Ensures there aren't new changes on the Rest adapter structure
        verifyNoMoreInteractions(retrofitFactory);

        // Ensures Observer and Requests are working as expected
        List<Item> events = subscriber.getOnNextEvents();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertCompleted();
        subscriber.assertUnsubscribed();
        Assert.assertEquals(1, server.getRequestCount());
        Assert.assertEquals("/add/path", request.getPath());
        Assert.assertNotNull(events);
        Assert.assertEquals(1, events.size());
    }

    /**
     * Ensures a 500 exception is thrown as an event through the observable stream
     */
    @Test
    public void testRepositoryGot500Response() {
        // Preconditions
        TestSubscriber<ListOfItems> subscriber = new TestSubscriber<>();
        MockResponseDispatcher.RETURN_500 = true;

        // Attaches the subscriber and executes the Observable.
        repository.getAllItems().subscribe(subscriber);

        // We ensure we've got 500 Server Error in the onError method of the Subscriber.
        subscriber.assertError(HttpException.class);
        subscriber.assertNoValues();
        subscriber.assertUnsubscribed();
        List<Throwable> exceptions = subscriber.getOnErrorEvents();
        //noinspection all
        Assert.assertEquals(500, ((HttpException) exceptions.get(0)).code());
    }

    /**
     * Ensures a 404 exception is thrown as an event through the observable stream
     */
    @Test
    public void testRepositoryGot404Response() {
        // Preconditions
        TestSubscriber<Item> subscriber = new TestSubscriber<>();

        // Attaches the subscriber and executes the Observable.
        repository.deleteItem(50).subscribe(subscriber);

        // We ensure we've got 404 Not Found in the onError method of the Subscriber.
        subscriber.assertError(HttpException.class);
        subscriber.assertNoValues();
        subscriber.assertUnsubscribed();
        List<Throwable> exceptions = subscriber.getOnErrorEvents();
        //noinspection all
        Assert.assertEquals(404, ((HttpException) exceptions.get(0)).code());
    }
}
