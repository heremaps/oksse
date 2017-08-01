package com.here.oksse;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class RealServerSentEventTest {

    private static final String TEST_URL = "http://www.test.com";

    @Mock
    private OkHttpClient mockClient;
    @Mock
    private RealServerSentEvent.Listener listener;
    @Mock
    private Call mockCall;

    private RealServerSentEvent testSse;
    private Request request;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        request = new Request.Builder().url(TEST_URL).build();
        testSse = new RealServerSentEvent(request, listener);
    }

    @Test
    public void test_WhenConnectFirstTime_NewCallIsEnqueuedWithHeaders() throws Exception {
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        testSse.connect(mockClient);

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(mockClient).newCall(requestCaptor.capture());

        assertThat(requestCaptor.getValue().url().toString(), containsString(TEST_URL));

        Headers headers = requestCaptor.getValue().headers();
        assertThat(headers.get("Accept-Encoding"), is(""));
        assertThat(headers.get("Accept"), is("text/event-stream"));
        assertThat(headers.get("Cache-Control"), is("no-cache"));
    }
}