package com.gzx.netty.client.handler;

import com.gzx.netty.transfer.request.FullGRequest;
import com.gzx.netty.transfer.request.GRequestHeader;
import com.gzx.netty.transfer.response.FullGResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/20 11:39
 * @Version V1.0
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullGResponse){
            FullGResponse fullGResponse = (FullGResponse) msg;
            System.out.println(fullGResponse.getGResponseHeader().getStatusCode());
            ByteBuf bodyContent = fullGResponse.getBodyContent();
            do {
                System.out.println((char)fullGResponse.getBodyContent().readByte());
            }while (bodyContent.isReadable());
            ((FullGResponse) msg).release();
        }
        ctx.close();
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        byte[] success = "success".getBytes();
        ByteBuf byteBuf = ctx.alloc().buffer(256);
        byteBuf.writeBytes(success);

        GRequestHeader gRequestHeader = new GRequestHeader("/aaaa", success.length);

        FullGRequest fullGRequest= new FullGRequest(gRequestHeader , byteBuf);
        HashMap map = new HashMap<String , String>();
        gRequestHeader.setParamters(map);
        map.put("123" , "456");

        ctx.write(fullGRequest);

        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
