package com.github.rpc.transport;

import com.github.rpc.spi.ServiceSupport;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RequestHandlerRegistry {
    private Map<Integer, RequestHandler> handlerMap = new HashMap<>();
    private static RequestHandlerRegistry instance = null;
    public static RequestHandlerRegistry getInstance() {
        if (null == instance) {
            instance = new RequestHandlerRegistry();
        }
        return instance;
    }

    private RequestHandlerRegistry() {
        Collection<RequestHandler> requestHandlers = ServiceSupport.loadAll(RequestHandler.class);
        for (RequestHandler requestHandler : requestHandlers) {
            handlerMap.put(requestHandler.type(), requestHandler);
            log.info("Load request handler, type: {}, class: {}.", requestHandler.type(), requestHandler.getClass().getCanonicalName());
        }
    }


    public RequestHandler get(int type) {
        return handlerMap.get(type);
    }
}
