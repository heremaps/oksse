# Introduction

OkSse is an extension library for OkHttp to create a Server-Sent Event (SSE) client

[Server-sent events](https://www.w3.org/TR/eventsource/) is a standard describing how servers can initiate data transmission towards clients once an initial client connection has been established. They are commonly used to send message updates or continuous data streams to a client.

# Integration

Add JitPack repository:

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add OkSse dependency:
```groovy
dependencies {
    compile 'com.github.heremaps:oksse:master-SNAPSHOT'
}
```

# Usage

You can create an OkSse instance either using an already existing OkHttp client instance, or let OkSse create a default one instead.

The following code creats a request that points to our SSE server and uses the OkSse instance to create a new ServerSentEvent connection:

```java
Request request = new Request.Builder().url(path).build();
OkSse okSse = new OkSse();
ServerSentEvent sse = okSse.newServerSentEvent(request, listener);
```

This implements the ServerSentEvent listener to get notified of the channel status and the received messages or comments:

```java
new ServerSentEvent.Listener() {
    @Override
    public void onOpen(ServerSentEvent sse, Response response) {
        // When the channel is opened
    }

    @Override
    public void onMessage(ServerSentEvent sse, String id, String event, String message) {
        // When a message is received
    }

    @WorkerThread
    @Override
    public void onComment(ServerSentEvent sse, String comment) {
       // When a comment is received
    }

    @WorkerThread
    @Override
    public boolean onRetryTime(ServerSentEvent sse, long milliseconds) {
        return true; // True to use the new retry time received by SSE
    }

    @WorkerThread
    @Override
    public boolean onRetryError(ServerSentEvent sse, Throwable throwable, Response response) {
        return true; // True to retry, false otherwise
    }

    @WorkerThread
    @Override
    public void onClosed(ServerSentEvent sse) {
        // Channel closed
    }
```

When done listing from SSE, remember to close the channel and clear resources:


```java
sse.close();
```

Once closed, you will need to create a new instance.

# License

Copyright (c) 2017 HERE Europe B.V.

Please see the [LICENSE](./LICENSE) file for details.
