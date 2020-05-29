package com.gzx.netty.transfer.request;


import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FullGRequest implements ReferenceCounted {
    public FullGRequest(GRequestHeader gRequestHeader, ByteBuf bodyContent) {
        this.gRequestHeader = gRequestHeader;
        this.bodyContent = bodyContent;
    }

    private GRequestHeader gRequestHeader;
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
