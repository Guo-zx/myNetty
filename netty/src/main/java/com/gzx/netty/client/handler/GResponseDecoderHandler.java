package com.gzx.netty.client.handler;

import com.gzx.netty.handler.decoder.GMessageDecoderHandler;
import com.gzx.netty.transfer.GMessageContent;
import com.gzx.netty.transfer.response.GResponseContent;
import com.gzx.netty.transfer.response.GResponseHeader;
import com.gzx.netty.transfer.response.GResponseLastContent;
import io.netty.buffer.ByteBuf;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/25 18:34
 * @Version V1.0
 */
public class GResponseDecoderHandler extends GMessageDecoderHandler<GResponseHeader,GMessageContent> {

    protected GResponseHeader createRejectMessage() {
        return new GResponseHeader(500, 0);
    }

    protected GResponseHeader createMessage(String[] initialLine) {
        return new GResponseHeader(Long.parseLong(initialLine[0]) , Long.parseLong(initialLine[1]));
    }

    @Override
    protected GMessageContent createMessageContent(ByteBuf content) {
        return new GResponseContent(content);
    }

    @Override
    protected GMessageContent createMessageLastContent(ByteBuf content) {
        return new GResponseLastContent(content);
    }
}
