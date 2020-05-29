package com.gzx.netty.constants;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/26 13:41
 * @Version V1.0
 */
public class GConstants {

    /**
     * 回车
     */
    public static final byte CR = 13;


    /**
     * 换行
     */
    public static final byte LF = 10;

    /**
     * 等号
     */
    public static final byte EQ = 61;

    /**
     * 空格
     */
    public static final byte SP = 32;

    /**
     * 回车 + 换行
     */
    public static final int CRLF_SHORT = (GConstants.CR << 8) | GConstants.LF;

}
