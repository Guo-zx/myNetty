package com.gzx.netty.transfer.request;

import com.gzx.netty.transfer.GMessage;
import com.gzx.netty.transfer.GMessageContent;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/26 18:24
 * @Version V1.0
 */
@Getter
@Setter
public class GRequestContent extends GMessageContent {

    public GRequestContent() {
    }

    public GRequestContent(ByteBuf content) {
        super(content);
    }
}
