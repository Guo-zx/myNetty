package com.gzx.netty.server.handler;

import com.gzx.netty.transfer.request.FullGRequest;
import com.gzx.netty.transfer.response.FullGResponse;
import com.gzx.netty.transfer.response.GResponseHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/27 12:10
 * @Version V1.0
 */
public class GMessageServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullGRequest){
            FullGRequest fullGRequest = (FullGRequest) msg;
            System.out.println(fullGRequest.getGRequestHeader().getURL());
            ByteBuf bodyContent = fullGRequest.getContent();
            do {
                System.out.println((char)fullGRequest.getContent().readByte());
            }while (bodyContent.isReadable());

            Map<String, String> paramters = fullGRequest.getGRequestHeader().getParamters();

            byte[] success = "success".getBytes();
            ByteBuf byteBuf = ctx.alloc().buffer(256);
            byteBuf.writeBytes(success);

            GResponseHeader gResponseHeader = new GResponseHeader(200, success.length);

            FullGResponse fullGResponse = new FullGResponse(gResponseHeader , byteBuf);
            ctx.writeAndFlush(fullGResponse);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
