package com.gzx.netty.transfer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @Author guozhixuan
 * @Description
 * @Date 2020/6/1 10:27
 * @Version V1.0
 */
@Getter
@Setter
@NoArgsConstructor
public class GMessageLengthAndParamters extends GMessage {

    private long length;

    private Map<String,String> paramters;

    public GMessageLengthAndParamters(long length) {
        this.length = length;
    }

    public GMessageLengthAndParamters(long length, Map<String, String> paramters) {
        this.length = length;
        this.paramters = paramters;
    }
}
