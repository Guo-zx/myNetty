package com.gzx.netty.transfer.response;


import com.gzx.netty.transfer.GMessageLengthAndParamters;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 *  状态码 长度/r/n
 *  返回参数（key/value）/r/n
 *  参数/r/n
 */
@Getter
@Setter
@NoArgsConstructor
public class GResponseHeader extends GMessageLengthAndParamters {
    public GResponseHeader(long statusCode, long length) {
        super(length);
        this.statusCode = statusCode;
    }

    private long statusCode;


}
