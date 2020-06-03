package com.gzx.netty.client.handler;

import com.gzx.netty.handler.aggregator.GMessageDecoderAggregatorHandler;
import com.gzx.netty.transfer.response.FullGResponse;
import com.gzx.netty.transfer.response.GResponseContent;
import com.gzx.netty.transfer.response.GResponseHeader;
import com.gzx.netty.transfer.response.GResponseLastContent;
import io.netty.buffer.ByteBuf;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/26 18:34
 * @Version V1.0
 */
public class GResponseDecoderAggregatorHandler extends GMessageDecoderAggregatorHandler<GResponseHeader, GResponseContent, GResponseLastContent, FullGResponse> {


    public GResponseDecoderAggregatorHandler(Class thisAcceptClass, int maxContentLength) {
        super(thisAcceptClass, maxContentLength);
    }


    protected FullGResponse beginAggregation(GResponseHeader start, ByteBuf content) {
        return new FullGResponse(start, content);
    }
}
