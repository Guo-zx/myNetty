package com.gzx.netty.transfer.response;

import io.netty.buffer.ByteBuf;
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
@NoArgsConstructor
public class GResponseLastContent extends GResponseContent {


    public GResponseLastContent(ByteBuf content) {
        super(content);
        if (content == null) {
            throw new NullPointerException("content");
        }
    }

}
