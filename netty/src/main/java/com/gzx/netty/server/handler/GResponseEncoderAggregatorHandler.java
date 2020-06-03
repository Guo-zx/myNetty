package com.gzx.netty.server.handler;

import com.gzx.netty.handler.aggregator.GMessageEncoderAggregatorHandler;
import com.gzx.netty.listpool.ListPool;
import com.gzx.netty.transfer.request.FullGRequest;
import com.gzx.netty.transfer.request.GRequestHeader;
import com.gzx.netty.transfer.response.FullGResponse;
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
public class GResponseEncoderAggregatorHandler extends GMessageEncoderAggregatorHandler<FullGResponse> {


    public GResponseEncoderAggregatorHandler() {
        super(FullGResponse.class);
    }


    @Override
    protected void encoderFullGMessage(FullGResponse fullGResponse, List out) {
        Optional.ofNullable(fullGResponse.getGResponseHeader()).ifPresent(out::add);
        Optional.ofNullable(fullGResponse.getContent()).ifPresent(out::add);
    }
}
