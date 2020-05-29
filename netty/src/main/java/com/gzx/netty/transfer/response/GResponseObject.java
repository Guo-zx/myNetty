package com.gzx.netty.transfer.response;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.DecoderResultProvider;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/26 18:45
 * @Version V1.0
 */
public class GResponseObject implements DecoderResultProvider {


    private DecoderResult decoderResult;

    @Override
    public DecoderResult decoderResult() {
        return this.decoderResult;
    }

    @Override
    public void setDecoderResult(DecoderResult decoderResult) {
        if (decoderResult == null) {
            throw new NullPointerException("decoderResult");
        }
        this.decoderResult = decoderResult;
    }

}
