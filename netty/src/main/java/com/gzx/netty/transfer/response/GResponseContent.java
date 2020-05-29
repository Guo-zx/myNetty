package com.gzx.netty.transfer.response;

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
@NoArgsConstructor
public class GResponseContent extends GResponseObject implements ReferenceCounted {

    ByteBuf content;


    public GResponseContent(ByteBuf content) {
        if (content == null) {
            throw new NullPointerException("content");
        }
        this.content = content;
    }

    @Override
    public int refCnt() {
        return 1;
    }

    @Override
    public ReferenceCounted retain() {
        return content.retain();
    }

    @Override
    public ReferenceCounted retain(int increment) {
        return content.retain(increment);
    }

    @Override
    public ReferenceCounted touch() {
        return this;
    }

    @Override
    public ReferenceCounted touch(Object hint) {
        return this;
    }

    @Override
    public boolean release() {
        return content.release();
    }

    @Override
    public boolean release(int decrement) {
        return content.release(decrement);
    }

}
