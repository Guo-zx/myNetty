package com.gzx.netty.transfer.response;


import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FullGResponse implements ReferenceCounted {
    public FullGResponse(GResponseHeader gResponseHeader, ByteBuf bodyContent) {
        this.gResponseHeader = gResponseHeader;
        this.bodyContent = bodyContent;
    }

    private GResponseHeader gResponseHeader;
    private ByteBuf bodyContent;


    @Override
    public int refCnt() {
        return 1;
    }

    @Override
    public ReferenceCounted retain() {
        return bodyContent.retain();
    }

    @Override
    public ReferenceCounted retain(int increment) {
        return bodyContent.retain(increment);
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
        return bodyContent.release();
    }

    @Override
    public boolean release(int decrement) {
        return bodyContent.release(decrement);
    }
}
