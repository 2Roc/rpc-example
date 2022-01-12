
package com.github.rpc.client.stubs;

import com.github.rpc.client.RequestIdSupport;
import com.github.rpc.client.ServiceStub;
import com.github.rpc.client.ServiceTypes;
import com.github.rpc.serialize.SerializeSupport;
import com.github.rpc.transport.Transport;
import com.github.rpc.transport.command.Code;
import com.github.rpc.transport.command.Command;
import com.github.rpc.transport.command.Header;
import com.github.rpc.transport.command.ResponseHeader;

import java.util.concurrent.ExecutionException;

public abstract class AbstractStub implements ServiceStub {
    protected Transport transport;

    protected byte [] invokeRemote(RpcRequest request) {
        Header header = new Header(ServiceTypes.TYPE_RPC_REQUEST, 1, RequestIdSupport.next());
        byte [] payload = SerializeSupport.serialize(request);
        Command requestCommand = new Command(header, payload);
        try {
            Command responseCommand = transport.send(requestCommand).get();
            ResponseHeader responseHeader = (ResponseHeader) responseCommand.getHeader();
            if(responseHeader.getCode() == Code.SUCCESS.getCode()) {
                return responseCommand.getPayload();
            } else {
                throw new Exception(responseHeader.getError());
            }

        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setTransport(Transport transport) {
        this.transport = transport;
    }
}
