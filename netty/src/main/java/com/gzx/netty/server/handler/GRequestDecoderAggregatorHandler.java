package com.gzx.netty.server.handler;

import com.gzx.netty.handler.aggregator.GMessageDecoderAggregatorHandler;
import com.gzx.netty.transfer.request.FullGRequest;
import com.gzx.netty.transfer.request.GRequestContent;
import com.gzx.netty.transfer.request.GRequestHeader;
import com.gzx.netty.transfer.request.GRequestLastContent;
import io.netty.buffer.ByteBuf;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/26 18:34
 * @Version V1.0
 */
public class GRequestDecoderAggregatorHandler extends GMessageDecoderAggregatorHandler<GRequestHeader, GRequestContent, GRequestLastContent, FullGRequest> {

    public GRequestDecoderAggregatorHandler() {
    }

    public GRequestDecoderAggregatorHandler(Class thisAcceptClass, int maxContentLength) {
        super(thisAcceptClass, maxContentLength);
    }

    protected FullGRequest beginAggregation(GRequestHeader start, ByteBuf content) {
        return new FullGRequest(start, content);
    }
}
