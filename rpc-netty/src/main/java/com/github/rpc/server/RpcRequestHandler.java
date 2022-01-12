
package com.github.rpc.server;

import com.github.rpc.client.ServiceTypes;
import com.github.rpc.client.stubs.RpcRequest;
import com.github.rpc.serialize.SerializeSupport;
import com.github.rpc.spi.Singleton;
import com.github.rpc.transport.RequestHandler;
import com.github.rpc.transport.command.Code;
import com.github.rpc.transport.command.Command;
import com.github.rpc.transport.command.Header;
import com.github.rpc.transport.command.ResponseHeader;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Singleton
@Slf4j
public class RpcRequestHandler implements RequestHandler, ServiceProviderRegistry {
    private Map<String/*service name*/, Object/*service provider*/> serviceProviders = new HashMap<>();

    @Override
    public Command handle(Command requestCommand) {
        Header header = requestCommand.getHeader();
        // 从payload中反序列化RpcRequest
        RpcRequest rpcRequest = SerializeSupport.parse(requestCommand.getPayload());
        try {
            // 查找所有已注册的服务提供方，寻找rpcRequest中需要的服务
            Object serviceProvider = serviceProviders.get(rpcRequest.getInterfaceName());
            if(serviceProvider != null) {
                // 找到服务提供者，利用Java反射机制调用服务的对应方法
                String arg = SerializeSupport.parse(rpcRequest.getSerializedArguments());
                Method method = serviceProvider.getClass().getMethod(rpcRequest.getMethodName(), String.class);
                String result = (String ) method.invoke(serviceProvider, arg);
                // 把结果封装成响应命令并返回
                return new Command(new ResponseHeader(type(), header.getVersion(), header.getRequestId()), SerializeSupport.serialize(result));
            }
            // 如果没找到，返回NO_PROVIDER错误响应。
            log.warn("No service Provider of {}#{}(String)!", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
            return new Command(new ResponseHeader(type(), header.getVersion(), header.getRequestId(), Code.NO_PROVIDER.getCode(), "No provider!"), new byte[0]);
        } catch (Throwable t) {
            // 发生异常，返回UNKNOWN_ERROR错误响应。
            log.warn("Exception: ", t);
            return new Command(new ResponseHeader(type(), header.getVersion(), header.getRequestId(), Code.UNKNOWN_ERROR.getCode(), t.getMessage()), new byte[0]);
        }
    }

    @Override
    public int type() {
        return ServiceTypes.TYPE_RPC_REQUEST;
    }

    @Override
    public synchronized <T> void addServiceProvider(Class<? extends T> serviceClass, T serviceProvider) {
        serviceProviders.put(serviceClass.getCanonicalName(), serviceProvider);
        log.info("Add service: {}, provider: {}.",
                serviceClass.getCanonicalName(),
                serviceProvider.getClass().getCanonicalName());
    }
}
