package com.gzx.netty.handler.decoder;

import com.gzx.netty.constants.GConstants;
import com.gzx.netty.transfer.GMessageContent;
import com.gzx.netty.transfer.GMessageLengthAndParamters;
import com.gzx.netty.transfer.request.GRequestContent;
import com.gzx.netty.transfer.request.GRequestLastContent;
import com.gzx.netty.transfer.response.GResponseLastContent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.util.ByteProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author GZX
 * @Description
 * @Date 2020/5/25 18:34
 * @Version V1.0
 */
public abstract class GMessageDecoderHandler<T extends GMessageLengthAndParamters,I extends GMessageContent> extends AbstractGMessageDecoderHandler {


    private T gMessageHeader;

    // 判断是否需要重新读取一个新的request
    private volatile boolean resetRequested;


    private enum State {
        READ_HEADER,
        READ_BODY,
        READ_FIXED_LENGTH_CONTENT,
        BAD_MESSAGE,
    }

    private State currentState = State.READ_HEADER;

    // 临时保存解析后的response长度，方便后续计算使用
    private long gMessageLength;

    private HeaderParser headerParser;
    private ParamtersParser paramtersParser;

    public GMessageDecoderHandler() {

        headerParser = new HeaderParser(8192);
        paramtersParser = new ParamtersParser(8192);
    }

    @Override
    protected void decodeGMessage(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {

        switch (currentState) {
            case READ_HEADER: try {

                char[] lineChars = headerParser.parse(buffer);
                if (lineChars == null) {
                    return;
                }
                String[] initialLine = splitHeaderLine(lineChars);
                if (initialLine.length < 2) {
                    currentState = State.READ_HEADER;
                    return;
                }

                gMessageHeader = createMessage(initialLine);

                gMessageLength = gMessageHeader.getLength();

                currentState = State.READ_BODY;
                // fall-through
            } catch (Exception e) {
                out.add(rejectMessage(buffer, e));
                return;
            }
            case READ_BODY: try {
                State nextState = readParamters(buffer);

                gMessageHeader.setDecoderResult(DecoderResult.SUCCESS);
                out.add(gMessageHeader);

                if (nextState == null) {
                    return;
                }

                currentState = nextState;
            } catch (Exception e) {
                out.add(rejectMessage(buffer, e));
                return;
            }
            case READ_FIXED_LENGTH_CONTENT: {
                int readLimit = buffer.readableBytes();

                if (readLimit == 0) {
                    return;
                }

                if (readLimit > gMessageLength) {
                    readLimit = (int) gMessageLength;
                }
                // 返回一个新的ByteBuf
                ByteBuf content = buffer.readRetainedSlice(readLimit);
                gMessageLength -= readLimit;

                if (gMessageLength == 0) {
                    // Read all content.
//                    GRequestLastContent gRequestLastContent = new GRequestLastContent(content);
                    I lastContent= createMessageLastContent(content);
                    lastContent.setDecoderResult(DecoderResult.SUCCESS);
                    out.add(lastContent);
                    resetGRequest();
                } else {
//                    GRequestContent gRequestContent = new GRequestContent(content);
                    I gtContent= createMessageContent(content);
                    gtContent.setDecoderResult(DecoderResult.SUCCESS);
                    out.add(gtContent);
                }
                return;
            }
            case BAD_MESSAGE: {
                buffer.skipBytes(buffer.readableBytes());
                break;
            }
        }
    }

    private String name;
    private String value;

    private State readParamters(ByteBuf buffer) {
        T gMessageHeader = this.gMessageHeader;
        Map paramters =  new HashMap() ;
        gMessageHeader.setParamters(paramters);

        char[] paramtersChars = paramtersParser.parse(buffer);
        if (paramtersChars == null) {
            return null;
        }
        if (paramtersParser.getPos() > 0) {
            do {
                // 这里就不做换行的判断了
                if (name != null) {
                    if (paramters == null) {
                        paramters = new HashMap();
                    }
                    paramters.put(name, value);
                }

                // 此时只有/r/n
                if (paramtersParser.getPos() <= 2) {
                    break;
                }

                splitParamters(paramtersChars ,paramtersParser.getPos() );

                paramtersChars = paramtersParser.parse(buffer);
                if (paramtersChars == null) {
                    return State.READ_FIXED_LENGTH_CONTENT;
                }
            } while (paramtersChars.length > 0);
        }

        if (name != null) {
            paramters.put(name, value);
        }

        name = null;
        value = null;

        return State.READ_FIXED_LENGTH_CONTENT;
    }

    private void splitParamters(char[] paramtersChars , int length) {
        int nameStart;
        int nameEnd;
        int valueStart;
        int valueEnd;

        nameStart = findNonWhitespace(paramtersChars, 0);
        for (nameEnd = nameStart; nameEnd < length; nameEnd ++) {
            char ch = paramtersChars[nameEnd];
            if (ch == '=' || Character.isWhitespace(ch)) {
                break;
            }
        }


        name = new String(paramtersChars, nameStart, nameEnd - nameStart);
        valueStart = findNonWhitespace(paramtersChars,nameEnd + 1);
        if (valueStart == length) {
            value = "";
        } else {
            valueEnd = findEndOfString(paramtersChars);
            value = new String(paramtersChars, valueStart, valueEnd - valueStart );
        }

    }


    private T rejectMessage(ByteBuf in, Exception cause) {
        currentState = State.BAD_MESSAGE;

        // 跳过已经读取的bytebuf
        in.skipBytes(in.readableBytes());

        if (gMessageHeader == null) {
            gMessageHeader = createRejectMessage();
        }
        gMessageHeader.setDecoderResult(DecoderResult.failure(cause));

        T ret = gMessageHeader;
        gMessageHeader = null;
        return ret;
    }

    protected abstract T createRejectMessage();

    protected abstract T createMessage(String[] initialLine) ;

    protected abstract I createMessageContent(ByteBuf content) ;

    protected abstract I createMessageLastContent(ByteBuf content) ;

    private String[] splitHeaderLine(char[] lineChars) {
        int aStart;
        int aEnd;
        int bStart;
        int bEnd;

        aStart = findNonWhitespace(lineChars, 0);
        aEnd = findWhitespace(lineChars, aStart);

        bStart = findNonWhitespace(lineChars, aEnd);
        bEnd = findWhitespace(lineChars, bStart);

        return new String[] {
                new String(lineChars , aStart , aEnd - aStart),
                new String(lineChars , bStart , bEnd - bStart) };


    }

    // 从字符串末尾开始找，找到不为空的为止
    private static int findEndOfString(char[] chars) {
        for (int result = chars.length - 1; result > 0; --result) {
            if (!Character.isWhitespace(chars[result]) && chars[result]!= Character.MIN_VALUE) {
                return result + 1;
            }
        }
        return 0;
    }

    private static int findNonWhitespace(char[] chars, int offset) {
        for (int result = offset; result < chars.length; ++result) {
            if (!Character.isWhitespace(chars[result])) {
                return result;
            }
        }
        return chars.length;
    }

    private static int findWhitespace(char[] chars, int offset) {
        for (int result = offset; result < chars.length; ++result) {
            if (Character.isWhitespace(chars[result])) {
                return result;
            }
        }
        return chars.length;
    }


    private void resetGRequest(){
        currentState = State.READ_HEADER;
        headerParser.reset();
        paramtersParser.reset();

        gMessageHeader = null;
    }


    private static class HeaderParser implements ByteProcessor {

        private char[] chars ;
        private int maxCharsCatacity;

        private int size;

        public HeaderParser(int maxCharsCatacity){
            this.maxCharsCatacity = maxCharsCatacity;
            this.chars = new char[maxCharsCatacity];
        }

        public char[] parse(ByteBuf buffer) {
            final int oldSize = size;
            pos = 0;
            int i = buffer.forEachByte(this);
            // 没有内容可以读到
            if (i == -1) {
                size = oldSize;
                return null;
            }
            buffer.readerIndex(i + 1);
            return chars;
        }

        public int getPos(){
            return this.pos;
        }

        public void reset() {
            for (int i = 0; i < chars.length; i++) {
                chars[i] = Character.MIN_VALUE;
            }
            size = 0;
            prevChar = null;
            pos = 0;
        }

        private Character prevChar ;

        private int pos;

        @Override
        public boolean process(byte value) throws Exception {
            // 将每个byte转换成char
            char nextByte = (char) (value & 0xFF);

            if (prevChar == null) {
                prevChar = nextByte;
            } else {
                if (prevChar == GConstants.CR && nextByte == GConstants.LF ) {
                    chars[pos] = nextByte;
                    pos ++ ;
                    return false;
                }
                prevChar = nextByte;
            }

            if (++ size > maxCharsCatacity) {
                throw new RuntimeException("header is larger than " + maxCharsCatacity + " bytes.");
            }

            chars[pos] = nextByte;
            pos ++ ;
            return true;
        }

    }

    private static class ParamtersParser extends HeaderParser {

        public ParamtersParser(int maxCharsCatacity){
            super(maxCharsCatacity);
        }

        public char[] parse(ByteBuf buffer) {
            reset();
            return super.parse(buffer);
        }

    }
}
