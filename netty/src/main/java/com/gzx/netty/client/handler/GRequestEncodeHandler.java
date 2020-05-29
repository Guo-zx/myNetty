package com.gzx.netty.client.handler;

import com.gzx.netty.constants.GConstants;
import com.gzx.netty.listpool.ListPool;
import com.gzx.netty.transfer.request.GRequestContent;
import com.gzx.netty.transfer.request.GRequestHeader;
import com.gzx.netty.transfer.request.GRequestLastContent;
import com.gzx.netty.transfer.request.GRequestObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.EncoderException;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.StringUtil;

import java.util.List;
import java.util.Map;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/27 12:18
 * @Version V1.0
 */
public class GRequestEncodeHandler extends ChannelOutboundHandlerAdapter {

    private ListPool listPool = new ListPool(16);

    private ENCODE_STATE state = ENCODE_STATE.STATE_INIT;

    enum ENCODE_STATE {
        STATE_INIT,ENCODE_BODY;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        List out = listPool.getListFormPool();
        try {
            if (msg instanceof GRequestObject) {
                GRequestObject cast = (GRequestObject) msg;
                try {
                    encode(ctx, cast, out);
                } finally {
                    ReferenceCountUtil.release(cast);
                }

                if (out.isEmpty()) {
                    listPool.cacheList2Pool(out);
                    out = null;

                    throw new EncoderException(
                            StringUtil.simpleClassName(this) + " must produce at least one message.");
                }
            } else {
                ctx.write(msg, promise);
            }
        } catch (EncoderException e) {
            throw e;
        } catch (Throwable t) {
            throw new EncoderException(t);
        } finally {
            if (out != null && out.size() > 0) {
                final int sizeMinusOne = out.size() - 1;
                if (sizeMinusOne == 0) {
                    ctx.write(out.get(0), promise);
                } else if (sizeMinusOne > 0) {
                    ChannelPromise voidPromise = ctx.voidPromise();
                    boolean isVoidPromise = promise == voidPromise;
                    for (int i = 0; i < sizeMinusOne; i ++) {
                        ChannelPromise p;
                        if (isVoidPromise) {
                            p = voidPromise;
                        } else {
                            p = ctx.newPromise();
                        }
                        ctx.write(out.get(i), p);
                    }
                    ctx.write(out.get(sizeMinusOne), promise);
                }
                listPool.cacheList2Pool(out);
            }
        }
    }

    private ByteBuf buf ;

    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {

        if (msg instanceof GRequestHeader) {
            if (state != ENCODE_STATE.STATE_INIT) {
                throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(msg));
            }

            GRequestHeader m = (GRequestHeader) msg;

            buf = ctx.alloc().buffer(256);
            // Encode the message.
            encodeGRequestHeader(buf, m);
            state = ENCODE_STATE.ENCODE_BODY;


            encodeGRequestParamters(m.getParamters(), buf);
            ByteBufUtil.writeShortBE(buf, GConstants.CRLF_SHORT);

        }

        if (msg instanceof ByteBuf) {
            buf.writeBytes((ByteBuf) msg);
            out.add(buf);
            buf = null;
        }

        if (msg instanceof GRequestContent) {
            switch (state) {
                case STATE_INIT:
                    throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(msg));
                case ENCODE_BODY:
                    long contentLength = ((GRequestContent) msg).getContent().readableBytes();
                    if (contentLength > 0) {
                        if (buf != null && buf.writableBytes() >= contentLength ) {
                            // merge into other buffer for performance reasons
                            buf.writeBytes(((GRequestContent) msg).getContent());
                            out.add(buf);
                        } else {
                            if (buf != null) {
                                out.add(buf);
                            }
                            out.add(((GRequestContent) msg).getContent());

                        }

                        if (msg instanceof GRequestLastContent) {
                            state = ENCODE_STATE.STATE_INIT;
                            buf = null;
                        }

                        break;
                    }

                default:
                    throw new Error();
            }

            if (msg instanceof GRequestLastContent) {
                state = ENCODE_STATE.STATE_INIT;
                buf = null;
            }
        } else if (buf != null) {
            out.add(buf);
        }
    }

    private void encodeGRequestParamters(Map<String, String> paramters, ByteBuf buf) {
        if (paramters != null && paramters.size() > 0) {
            paramters.forEach((key , value) -> {
                final int nameLen = key.length();
                final int valueLen = value.length();
                final int entryLen = nameLen + valueLen + 3;
                buf.ensureWritable(entryLen);
                int offset = buf.writerIndex();

                buf.writeBytes(key.getBytes());
                offset += nameLen;
                buf.writeByte(GConstants.EQ);
                offset += 1;

                buf.writeBytes(value.getBytes());
                offset += valueLen;
                ByteBufUtil.setShortBE(buf, offset, GConstants.CRLF_SHORT);
                offset += 2;
                buf.writerIndex(offset);
            });
        }
    }

    protected void encodeGRequestHeader(ByteBuf buf, GRequestHeader request) throws Exception {
        ByteBufUtil.copy(new AsciiString(request.getURL()), buf);
        buf.writeByte(GConstants.SP);
        long length = request.getLength();
        buf.writeBytes(String.valueOf(length).getBytes());
        ByteBufUtil.writeShortBE(buf, GConstants.CRLF_SHORT);
    }

}
