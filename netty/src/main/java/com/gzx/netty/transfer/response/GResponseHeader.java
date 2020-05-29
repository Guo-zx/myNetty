package com.gzx.netty.transfer.response;


import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 *  状态码 长度/r/n
 *  返回参数（key/value）/r/n
 *  参数/r/n
 */
@Getter
@Setter
public class GResponseHeader extends GResponseObject {
    public GResponseHeader(long statusCode, long length) {
        this.statusCode = statusCode;
        this.length = length;
    }

    private long statusCode;
    private long length;
    private Map<String,String> paramters;






}
