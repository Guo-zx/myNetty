package com.gzx.netty.transfer.request;


import com.gzx.netty.transfer.FullGMessage;
import com.gzx.netty.transfer.GMessageContent;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
public class FullGRequest extends FullGMessage {
    public FullGRequest(GRequestHeader gRequestHeader, ByteBuf content) {
        super(content);
        this.gRequestHeader = gRequestHeader;
    }

    private GRequestHeader gRequestHeader;

}
