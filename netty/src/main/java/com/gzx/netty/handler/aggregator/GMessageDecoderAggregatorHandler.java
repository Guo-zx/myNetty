package com.gzx.netty.handler.aggregator;

import com.gzx.netty.handler.utils.ClassGenericMatcher;
import com.gzx.netty.listpool.ListPool;
import com.gzx.netty.transfer.FullGMessage;
import com.gzx.netty.transfer.GMessageContent;
import com.gzx.netty.transfer.GMessageLengthAndParamters;
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
public abstract class GMessageDecoderAggregatorHandler<T extends GMessageLengthAndParamters,I extends GMessageContent,K,R extends FullGMessage> extends ChannelInboundHandlerAdapter {


    private ListPool listPool = new ListPool(16);

    private Class thisAcceptClass;

    private R currentMessage;

    private int maxContentLength;

    private int maxCumulationBufferComponents = 1024;

    public GMessageDecoderAggregatorHandler() {
    }

    public GMessageDecoderAggregatorHandler(Class thisAcceptClass, int maxContentLength){
        this.thisAcceptClass = thisAcceptClass;
        this.maxContentLength = maxContentLength;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        List out = listPool.getListFormPool();
        try {
            if (thisAcceptClass.isInstance(msg)) {
                try {
                    decodeGMessage(ctx, msg, out);
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


    private void decodeGMessage(ChannelHandlerContext ctx, Object msg, List out) {

        if (ClassGenericMatcher.isAcceptClass(this, GMessageDecoderAggregatorHandler.class,"T" , msg , ctx)) {
            if (currentMessage != null) {
//                currentMessage.release();
                currentMessage = null;
                throw new MessageAggregationException();
            }

            T m = (T) msg;

            // 这里就不做100continue的处理了


            if (m.getLength()  > maxContentLength) {
                ctx.fireExceptionCaught(
                        new RuntimeException("contentLength larger than maxContentLength , contentLength is "+ m.getLength() +" bytes. maxContentLength is " + maxContentLength + " bytes."));
                ReferenceCountUtil.release(m);
                return;
            }

            // 如果发现解析失败了
            if (m instanceof DecoderResultProvider && !((DecoderResultProvider) m).decoderResult().isSuccess()) {
                R aggregated = beginAggregation(m, Unpooled.EMPTY_BUFFER);
                out.add(aggregated);
                return;
            }

            // 创建一个复合的bytebuf，收集body体中的byte
            CompositeByteBuf content = ctx.alloc().compositeBuffer(maxCumulationBufferComponents);

            // 这里就不把header的bytebuf加入到CompositeByteBuf中了

            currentMessage = beginAggregation(m, content);
        } else if (ClassGenericMatcher.isAcceptClass(this, GMessageDecoderAggregatorHandler.class,"I" , msg , ctx)) {
            if (currentMessage == null) {
                return;
            }

            CompositeByteBuf compositeByteBuf = (CompositeByteBuf) currentMessage.getContent();

            I m = (I) msg;
            // 如果CompositeByteBuf不能存储消息体则抛出异常给下一个handler处理
            if (compositeByteBuf.readableBytes() > maxContentLength - m.getContent().readableBytes()) {
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
                    last = ClassGenericMatcher.isAcceptClass(this, GMessageDecoderAggregatorHandler.class,"K" , m , ctx);
                }
            } else {
                last = ClassGenericMatcher.isAcceptClass(this, GMessageDecoderAggregatorHandler.class,"K" , m , ctx);
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


    protected abstract R beginAggregation(T start, ByteBuf content) ;
}
