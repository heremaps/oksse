/*
 *  Copyright (c) 2016 HERE Europe B.V.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.here.oksse;

import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Test for {@link OkSse}
 */
public class ITOkSse {

    private final static String URL = "https://proxy.streamdata.io/https://api.myjson.com/bins/jixid";

    private static final long TEST_TIMEOUT = TimeUnit.SECONDS.toMillis(60);

    private final OkSse okSse = new OkSse();

    private boolean isAlive;
    private boolean hasOpened;
    private boolean hasReceivedMessage;

    @Test
    public void testNewServerSentEventConnection() throws Exception {
        isAlive = true;
        Request request = new Request.Builder().url(URL).build();
        ServerSentEvent sse = okSse.newServerSentEvent(request, new ServerSentEvent.Listener() {
            @Override
            public void onOpen(ServerSentEvent sse, Response response) {
                System.out.println("OkSse opened: " + response.message());
                hasOpened = true;
            }

            @Override
            public void onMessage(ServerSentEvent sse, String id, String event, String message) {
                System.out.println("New OkSse message id=" + id + " event=" + event + " message=" + message);
                assert message.equals("{\"name\":\"oksse\",\"test\":1}");
                hasReceivedMessage = true;
            }

            @Override
            public void onComment(ServerSentEvent sse, String comment) {
                System.out.println("New OkSse comment " + comment);
            }

            @Override
            public boolean onRetryTime(ServerSentEvent sse, long milliseconds) {
                System.out.println("OkSse sends retry time " + milliseconds + " milliseconds");
                return true;
            }

            @Override
            public boolean onRetryError(ServerSentEvent sse, Throwable throwable, Response response) {
                throw new RuntimeException(throwable);
            }

            @Override
            public void onClosed(ServerSentEvent sse) {
                isAlive = false;
                System.out.println("OkSse connection closed");
            }

            @Override
            public Request onPreRetry(ServerSentEvent sse, Request originalRequest) {
                throw new RuntimeException("No retry was expected");
            }

        });

        long startTime = System.currentTimeMillis();
        while (isAlive) {
            try {
                Thread.sleep(100);
                if (System.currentTimeMillis() - startTime > TEST_TIMEOUT || (hasOpened && hasReceivedMessage)) {
                    sse.close();
                }
            } catch (InterruptedException e) {
                isAlive = false;
            }
        }

        assert hasOpened;
        assert hasReceivedMessage;
    }

}
