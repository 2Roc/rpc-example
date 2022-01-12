package com.github.rpc.transport.netty;

import com.github.rpc.transport.command.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class RequestDecoder extends CommandDecoder {

    @Override
    protected Header decodeHeader(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
        return new Header(
                byteBuf.readInt(),
                byteBuf.readInt(),
                byteBuf.readInt()
        );
    }
}
