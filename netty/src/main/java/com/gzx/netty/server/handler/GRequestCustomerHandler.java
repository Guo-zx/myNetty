package com.gzx.netty.server.handler;

import com.gzx.netty.transfer.request.FullGRequest;
import com.gzx.netty.transfer.response.FullGResponse;
import com.gzx.netty.transfer.response.GResponseHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/27 12:10
 * @Version V1.0
 */
public class GRequestCustomerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullGRequest){
            FullGRequest fullGRequest = (FullGRequest) msg;
            System.out.println(fullGRequest.getGRequestHeader().getURL());
            ByteBuf bodyContent = fullGRequest.getBodyContent();
            do {
                System.out.println((char)fullGRequest.getBodyContent().readByte());
            }while (bodyContent.isReadable());

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
