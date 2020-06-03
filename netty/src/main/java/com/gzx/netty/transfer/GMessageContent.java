package com.gzx.netty.transfer;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.DecoderResultProvider;
import io.netty.util.ReferenceCounted;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author guozhixuan
 * @Description
 * @Date 2020/6/1 10:24
 * @Version V1.0
 */
@Getter
@Setter
public class GMessageContent extends GMessage   {


    ByteBuf content;

    public GMessageContent() {
    }

    public GMessageContent(ByteBuf content) {
        if (content == null) {
            throw new NullPointerException("content");
        }
        this.content = content;
    }

//    @Override
//    public int refCnt() {
//        return 1;
//    }
//
//    @Override
//    public ReferenceCounted retain() {
//        return content.retain();
//    }
//
//    @Override
//    public ReferenceCounted retain(int increment) {
//        return content.retain(increment);
//    }
//
//    @Override
//    public ReferenceCounted touch() {
//        return this;
//    }
//
//    @Override
//    public ReferenceCounted touch(Object hint) {
//        return this;
//    }
//
//    @Override
//    public boolean release() {
//        return content.release();
//    }
//
//    @Override
//    public boolean release(int decrement) {
//        return content.release(decrement);
//    }

}
