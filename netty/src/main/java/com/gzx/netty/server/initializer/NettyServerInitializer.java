package com.gzx.netty.server.initializer;

import com.gzx.netty.server.handler.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/20 11:33
 * @Version V1.0
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast(new GResponseEncodeHandler());
        channel.pipeline().addLast(new GResponseAggregatorHandler());
        channel.pipeline().addLast(new GRequestDecoderHandler());
        channel.pipeline().addLast(new GRequestAggregatorHandler(8192));
        channel.pipeline().addLast(new GRequestCustomerHandler());
    }
}
