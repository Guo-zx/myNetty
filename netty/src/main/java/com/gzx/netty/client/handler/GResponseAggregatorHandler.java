package com.gzx.netty.client.handler;

import com.gzx.netty.listpool.ListPool;
import com.gzx.netty.transfer.response.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.DecoderResultProvider;
import io.netty.handler.codec.MessageAggregationException;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/26 18:34
 * @Version V1.0
 */
public class GResponseAggregatorHandler extends ChannelInboundHandlerAdapter {


    private ListPool listPool = new ListPool(16);

    private Class thisAcceptClass;

    private FullGResponse currentMessage;

    private int maxContentLength;

    private int maxCumulationBufferComponents = 1024;

    public GResponseAggregatorHandler(int maxContentLength){
        this.thisAcceptClass = GResponseObject.class;
        this.maxContentLength = maxContentLength;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        List out = listPool.getListFormPool();
        try {
            if (thisAcceptClass.isInstance(msg)) {
                try {
                    decodeGResponse(ctx, msg, out);
                } finally {
                    ReferenceCountUtil.release(msg);
                }
            } else {
                out.add(msg);
            }
        } catch (DecoderException e) {
            throw e;
        } catch (Exception e) {
            throw new DecoderException(e);
        } finally {
            int size = out.size();
            for (int i = 0; i < size; i ++) {
                ctx.fireChannelRead(out.get(i));
            }
            listPool.cacheList2Pool(out);
        }
    }

    private void decodeGResponse(ChannelHandlerContext ctx, Object msg, List out) {

        if (msg instanceof GResponseHeader) {
            if (currentMessage != null) {
                currentMessage.release();
                currentMessage = null;
                throw new MessageAggregationException();
            }

            GResponseHeader m = (GResponseHeader) msg;

            // 这里就不做100continue的处理了


            if (m.getLength()  > maxContentLength) {
                ctx.fireExceptionCaught(
                        new RuntimeException("contentLength larger than maxContentLength , contentLength is "+ m.getLength() +" bytes. maxContentLength is " + maxContentLength + " bytes."));
                ReferenceCountUtil.release(m);
                return;
            }

            // 如果发现解析失败了
            if (m instanceof DecoderResultProvider && !((DecoderResultProvider) m).decoderResult().isSuccess()) {
                FullGResponse aggregated = beginAggregation(m, Unpooled.EMPTY_BUFFER);
                out.add(aggregated);
                return;
            }

            // 创建一个复合的bytebuf，收集body体中的byte
            CompositeByteBuf content = ctx.alloc().compositeBuffer(maxCumulationBufferComponents);

            // 这里就不把header的bytebuf加入到CompositeByteBuf中了

            currentMessage = beginAggregation(m, content);
        } else if (msg instanceof GResponseContent) {
            if (currentMessage == null) {
                return;
            }

            CompositeByteBuf compositeByteBuf = (CompositeByteBuf) currentMessage.getBodyContent();

            GResponseContent m = (GResponseContent) msg;
            // 如果CompositeByteBuf不能存储消息体则抛出异常给下一个handler处理
            if (compositeByteBuf.readableBytes() > maxContentLength - m.getContent().readableBytes()) {
                FullGResponse fullGResponse = (FullGResponse) currentMessage;
                ctx.fireExceptionCaught(
                        new RuntimeException("contentLength larger than maxContentLength , contentLength is "+ m.getContent().readableBytes() +" bytes. maxContentLength is " + maxContentLength + " bytes."));
                ReferenceCountUtil.release(m);
                return;
            }

            // 添加到compositeByteBuf中
            if (m.getContent().isReadable()) {
                compositeByteBuf.addComponent(true, m.getContent().retain());
            }

            boolean last;
            // 判断是否解析成功
            if (m instanceof DecoderResultProvider) {
                DecoderResult decoderResult = ((DecoderResultProvider) m).decoderResult();
                // 如果有失败的
                if (!decoderResult.isSuccess()) {
                    if (currentMessage instanceof DecoderResultProvider) {
                        ((DecoderResultProvider) currentMessage).setDecoderResult(
                                DecoderResult.failure(decoderResult.cause()));
                    }
                    last = true;
                } else {
                    last = m instanceof GResponseLastContent;
                }
            } else {
                last = m instanceof GResponseLastContent;
            }

            if (last) {
                // All done
                out.add(currentMessage);
                currentMessage = null;
            }
        } else {
            throw new MessageAggregationException();
        }

    }

    protected FullGResponse beginAggregation(GResponseHeader start, ByteBuf content) {
        return new FullGResponse( start, content);
    }
}
