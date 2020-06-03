package com.gzx.netty.handler.decoder;

import com.gzx.netty.listpool.ListPool;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderException;
import io.netty.util.internal.StringUtil;

import java.util.List;

/**
 * @Author guozhixuan
 * @Description
 * @Date 2020/6/1 10:03
 * @Version V1.0
 */
public abstract class AbstractGMessageDecoderHandler extends ChannelInboundHandlerAdapter {


    // 保存每次累加之后的ByteBuf 直到组成一个完整的消息体
    private ByteBuf cumulation;
    private boolean first;


    private ListPool listPool = new ListPool(16);

    // 计算已经读取了多少个bytebuf
    private int numReads;
    // 多少次读取之后释放bytebuff空间
    private int discardAfterReads = 16;


    private DECODE_STATE decodeState = DECODE_STATE.STATE_INIT;

    enum DECODE_STATE {
        STATE_INIT,STATE_CALLING_CHILD_DECODE,STATE_HANDLER_REMOVED_PENDING;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ByteBuf) {
            List out = listPool.getListFormPool();
            try {
                ByteBuf data = (ByteBuf) msg;
                first = cumulation == null;
                if (first) {
                    cumulation = data;
                } else {
                    cumulation = cumulateOld2NewByteBuf(ctx.alloc(), cumulation, data);
                }
                decode(ctx, cumulation, out);
            } catch (DecoderException e) {
                throw e;
            } catch (Exception e) {
                throw new DecoderException(e);
            } finally {
                if (cumulation != null && !cumulation.isReadable()) {
                    numReads = 0;
                    cumulation.release();
                    cumulation = null;
                } else if (++ numReads >= discardAfterReads) {
                    numReads = 0;
                    discardSomeReadBytes();
                }

                fireChannelRead(ctx, out);
                listPool.cacheList2Pool(out);
            }
        } else {
            ctx.fireChannelRead(msg);
        }

    }

    private void decode(ChannelHandlerContext ctx, ByteBuf cumulation, List out) {
        try {
            while (cumulation.isReadable()) {
                // 判断当前的缓存中是否还有数据向下个handler发送
                int outSize = out.size();

                if (outSize > 0) {
                    fireChannelRead(ctx, out);
                    out.clear();

                    outSize = 0;
                }

                int oldInputLength = cumulation.readableBytes();
                decodeMessage(ctx, cumulation, out);

                if (ctx.isRemoved()) {
                    break;
                }

                // 判断进行decode之后的数据是否能够生成出一个消息
                // 如果out的size为0，说明没有decode生成一个消息，所以判断是否需要进行下一次循环，
                // 如果还有byte可以读取，那么进行循环，如果没有则直接break，等待下次的进入channelRead方法的byte一起组成一个消息
                if (outSize == out.size()) {
                    if (oldInputLength == cumulation.readableBytes()) {
                        break;
                    } else {
                        continue;
                    }
                }

                // 进行到这里说明decode解析出内容了，如果此时byte可读取内容没有改变则直接抛出异常
                if (oldInputLength == cumulation.readableBytes()) {
                    throw new DecoderException(
                            StringUtil.simpleClassName(getClass()) +
                                    ".decode() did not read anything but decoded a message.");
                }

            }
        } catch (DecoderException e) {
            throw e;
        } catch (Exception cause) {
            throw new DecoderException(cause);
        }
    }


    void decodeMessage(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
            throws Exception {
        decodeState = DECODE_STATE.STATE_CALLING_CHILD_DECODE;
        try {
            decodeGMessage(ctx, in, out);
        } finally {
            boolean removePending = decodeState == DECODE_STATE.STATE_HANDLER_REMOVED_PENDING;
            decodeState = DECODE_STATE.STATE_INIT;
            // 如果此时handler被移除了，需要将累加的cumulation中的内容清理掉发给下一个handler
            if (removePending) {
                handlerRemoved(ctx);
            }
        }
    }

    protected abstract void decodeGMessage(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception;


    @Override
    public final void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 如果是正在调用decode，直接设置成STATE_HANDLER_REMOVED_PENDING之后，等decode执行完之后会进行判断，进入到下面的代码
        if (decodeState == DECODE_STATE.STATE_CALLING_CHILD_DECODE) {
            decodeState = DECODE_STATE.STATE_HANDLER_REMOVED_PENDING;
            return;
        }
        ByteBuf buf = cumulation;
        if (buf != null) {
            cumulation = null;

            // 如果还有消息可以读取，则直接将消息发送给下一个handler
            int readable = buf.readableBytes();
            if (readable > 0) {
                ByteBuf bytes = buf.readBytes(readable);
                buf.release();
                ctx.fireChannelRead(bytes);
            } else {
                buf.release();
            }

            // 重置numReads
            numReads = 0;

            // 消息读取完毕
            ctx.fireChannelReadComplete();
        }
    }

    void fireChannelRead(ChannelHandlerContext ctx, List msgs) {
        for (Object outObject: msgs) {
            ctx.fireChannelRead(outObject);
        }
    }

    private ByteBuf cumulateOld2NewByteBuf(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf data) {
        ByteBuf oldCumulation = cumulation;
        ByteBuf newBuffer = alloc.buffer(oldCumulation.readableBytes() + data.readableBytes());
        newBuffer.writeBytes(oldCumulation);
        oldCumulation.release();
        return newBuffer;
    }

    private void discardSomeReadBytes() {
        if (cumulation != null && !first && cumulation.refCnt() == 1) {
            cumulation.discardSomeReadBytes();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
