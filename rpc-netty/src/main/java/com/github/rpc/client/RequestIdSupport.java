
package com.github.rpc.client;

import java.util.concurrent.atomic.AtomicInteger;


public class RequestIdSupport {
    private final static AtomicInteger NEXT_REQUEST_ID = new AtomicInteger(0);
    public static int next() {
        return NEXT_REQUEST_ID .getAndIncrement();
    }
}
