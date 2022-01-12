package com.github.rpc.transport.netty;

import com.github.rpc.transport.RequestHandler;
import com.github.rpc.transport.RequestHandlerRegistry;
import com.github.rpc.transport.command.Command;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class RequestInvocation extends SimpleChannelInboundHandler<Command> {
    private final RequestHandlerRegistry requestHandlerRegistry;

    RequestInvocation(RequestHandlerRegistry requestHandlerRegistry) {
        this.requestHandlerRegistry = requestHandlerRegistry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command request) throws Exception {
        RequestHandler handler = requestHandlerRegistry.get(request.getHeader().getType());
        if(null != handler) {
            Command response = handler.handle(request);
            if(null != response) {
                channelHandlerContext.writeAndFlush(response).addListener((ChannelFutureListener) channelFuture -> {
                    if (!channelFuture.isSuccess()) {
                        log.warn("Write response failed!", channelFuture.cause());
                        channelHandlerContext.channel().close();
                    }
                });
            } else {
                log.warn("Response is null!");
            }
        } else {
            throw new Exception(String.format("No handler for request with type: %d!", request.getHeader().getType()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Exception: ", cause);

        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if(channel.isActive())ctx.close();
    }
}
