package com.gzx.netty.server.handler;

import com.gzx.netty.handler.decoder.GMessageDecoderHandler;
import com.gzx.netty.transfer.GMessageContent;
import com.gzx.netty.transfer.request.GRequestContent;
import com.gzx.netty.transfer.request.GRequestHeader;
import com.gzx.netty.transfer.request.GRequestLastContent;
import io.netty.buffer.ByteBuf;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/25 18:34
 * @Version V1.0
 */
public class GRequestDecoderHandler extends GMessageDecoderHandler<GRequestHeader,GRequestContent> {


    protected GRequestHeader createRejectMessage() {
        return new GRequestHeader("/", 0);
    }

    protected GRequestHeader createMessage(String[] initialLine) {
        return new GRequestHeader(initialLine[0], Long.parseLong(initialLine[1]));
    }

    protected GRequestContent createMessageContent(ByteBuf content) {
        return new GRequestContent(content);
    }

    protected GRequestLastContent createMessageLastContent(ByteBuf content) {
        return new GRequestLastContent(content);
    }
}
