package com.gzx.netty.transfer.request;


import com.gzx.netty.transfer.GMessageLengthAndParamters;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 *  请求URL 长度/r/n
 *  请求参数（key/value）/r/n
 *  参数/r/n
 */
@Getter
@Setter
public class GRequestHeader extends GMessageLengthAndParamters {
    public GRequestHeader(String URL, long length) {
        super(length);
        this.URL = URL;
    }

    public GRequestHeader(long length, Map<String, String> paramters, String URL) {
        super(length, paramters);
        this.URL = URL;
    }

    private String URL;


}
