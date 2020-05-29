package com.gzx.netty.client.initializer;

import com.gzx.netty.client.handler.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/20 11:41
 * @Version V1.0
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast(new GResponseDecoderHandler());
        channel.pipeline().addLast(new GResponseAggregatorHandler(8192));
        channel.pipeline().addLast(new GRequestEncodeHandler());
        channel.pipeline().addLast(new GRequestAggregatorHandler(8192));
        channel.pipeline().addLast(new NettyClientHandler());
    }
}