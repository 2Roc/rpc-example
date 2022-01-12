package com.github.rpc.client;

import com.github.rpc.transport.Transport;

public interface StubFactory {
    <T> T createStub(Transport transport, Class<T> serviceClass);
}
