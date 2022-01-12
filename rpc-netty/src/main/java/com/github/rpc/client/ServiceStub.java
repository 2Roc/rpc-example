package com.github.rpc.client;

import com.github.rpc.transport.Transport;


public interface ServiceStub {
    void setTransport(Transport transport);
}
