/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.interviewtest.feature;

import okhttp3.mockwebserver.MockWebServer;

class BaseUnitTestCase {

    MockWebServer server;

    public void setUp() throws Exception {
        server = new MockWebServer();
    }

    public void tearDown() throws Exception {
        server.shutdown();
    }
}
