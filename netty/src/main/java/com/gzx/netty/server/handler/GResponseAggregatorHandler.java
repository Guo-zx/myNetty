package com.gzx.netty.server.handler;

import com.gzx.netty.listpool.ListPool;
import com.gzx.netty.transfer.response.FullGResponse;
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
public class GResponseAggregatorHandler extends ChannelOutboundHandlerAdapter {


    private ListPool listPool = new ListPool(16);

    private Class thisAcceptClass;


    public GResponseAggregatorHandler() {
        this.thisAcceptClass = FullGResponse.class;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        List out = listPool.getListFormPool();
        try {
            if (thisAcceptClass.isInstance(msg)) {
                FullGResponse fullGResponse = (FullGResponse) msg;
                Optional.ofNullable(fullGResponse.getGResponseHeader()).ifPresent(out::add);
                Optional.ofNullable(fullGResponse.getBodyContent()).ifPresent(out::add);
            } else {
                out.add(msg);
            }
        } catch (DecoderException e) {
            throw e;
        } catch (Exception e) {
            throw new DecoderException(e);
        } finally {
            int size = out.size();
            for (int i = 0; i < size; i++) {
                ctx.write(out.get(i));
            }
            listPool.cacheList2Pool(out);
        }
    }

}
