package com.gzx.netty.transfer.response;


import com.gzx.netty.transfer.FullGMessage;
import com.gzx.netty.transfer.request.GRequestHeader;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FullGResponse extends FullGMessage {
    public FullGResponse(GResponseHeader gResponseHeader, ByteBuf content) {
        super(content);
        this.gResponseHeader = gResponseHeader;
    }

    private GResponseHeader gResponseHeader;

}
