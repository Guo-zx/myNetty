package com.gzx.netty.client.handler;

import com.gzx.netty.handler.aggregator.GMessageEncoderAggregatorHandler;
import com.gzx.netty.listpool.ListPool;
import com.gzx.netty.transfer.request.FullGRequest;
import com.gzx.netty.transfer.request.GRequestHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.DecoderException;

import java.util.List;
import java.util.Optional;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/26 18:34
 * @Version V1.0
 */
public class GRequestEncoderAggregatorHandler extends GMessageEncoderAggregatorHandler<FullGRequest> {


    public GRequestEncoderAggregatorHandler() {
        super(FullGRequest.class);
    }


    @Override
    protected void encoderFullGMessage(FullGRequest fullGRequest , List out) {
        Optional.ofNullable(fullGRequest.getGRequestHeader()).ifPresent(out::add);
        Optional.ofNullable(fullGRequest.getContent()).ifPresent(out::add);
    }
}
