package com.gzx.netty.transfer.request;


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
public class GRequestHeader extends GRequestObject  {
    public GRequestHeader(String URL, long length) {
        this.URL = URL;
        this.length = length;
    }

    private String URL;
    private long length;
    private Map<String,String> paramters;






}
