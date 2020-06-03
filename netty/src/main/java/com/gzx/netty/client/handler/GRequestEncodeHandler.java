package com.gzx.netty.client.handler;

import com.gzx.netty.constants.GConstants;
import com.gzx.netty.handler.encoder.GMessageEncodeHandler;
import com.gzx.netty.transfer.request.GRequestContent;
import com.gzx.netty.transfer.request.GRequestHeader;
import com.gzx.netty.transfer.request.GRequestLastContent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.AsciiString;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/27 12:18
 * @Version V1.0
 */
public class GRequestEncodeHandler extends GMessageEncodeHandler<GRequestHeader , GRequestContent, GRequestLastContent> {

    protected void encodeGMessageHeader(ByteBuf buf, GRequestHeader request) throws Exception {
        ByteBufUtil.copy(new AsciiString(request.getURL()), buf);
        buf.writeByte(GConstants.SP);
        long length = request.getLength();
        buf.writeBytes(String.valueOf(length).getBytes());
        ByteBufUtil.writeShortBE(buf, GConstants.CRLF_SHORT);
    }

}
