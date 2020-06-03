package com.gzx.netty.transfer;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.DecoderResultProvider;

/**
 * @Author guozhixuan
 * @Description
 * @Date 2020/6/1 10:24
 * @Version V1.0
 */
public class GMessage implements DecoderResultProvider {


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
