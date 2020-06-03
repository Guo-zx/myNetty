package com.gzx.netty.transfer.request;

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
public class GRequestLastContent extends GRequestContent{


    public GRequestLastContent(ByteBuf content) {
        super(content);
        if (content == null) {
            throw new NullPointerException("content");
        }
    }

}
