package com.gzx.netty.server.handler;

import com.gzx.netty.constants.GConstants;
import com.gzx.netty.handler.encoder.GMessageEncodeHandler;
import com.gzx.netty.listpool.ListPool;
import com.gzx.netty.transfer.response.GResponseContent;
import com.gzx.netty.transfer.response.GResponseHeader;
import com.gzx.netty.transfer.response.GResponseLastContent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.EncoderException;
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
public class GResponseEncodeHandler extends GMessageEncodeHandler<GResponseHeader , GResponseContent, GResponseLastContent> {


    protected void encodeGMessageHeader(ByteBuf buf, GResponseHeader response) throws Exception {
        buf.writeBytes(String.valueOf(response.getStatusCode()).getBytes());
        buf.writeByte(GConstants.SP);
        long length = response.getLength();
        buf.writeBytes(String.valueOf(length).getBytes());
        ByteBufUtil.writeShortBE(buf, GConstants.CRLF_SHORT);
    }

}
