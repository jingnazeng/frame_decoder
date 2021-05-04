package codec.frame.decoder;

import ch.qos.logback.classic.Logger;
import codec.frame.common.Frame;
import codec.frame.common.FrameType;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MessageFrameDecoder implements FrameDecoder {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(MessageFrameDecoder.class);

    private int MAX_BUFFER_CAPACITY = 256;
    private int MESSAGE_LENGTH_OFFSET = 0;
    private int MESSAGE_TYPE_OFFSET = 1;
    private int PAYLOAD_OFFSET = 2;

    private byte[] byteBuffer = new byte[MAX_BUFFER_CAPACITY];
    private boolean pendingIncompleteFrame;
    private int existingLength;
    private int expectedLength;

    /**
     * Returns an List of <a{@link Frame}</a>  object as bytes arrives.
     *
     * @param bytes     a byte buffer containing zero or more <a{@link Frame}</a> objects.
     * @return  output    an List of <a{@link Frame}</a> objects
     */

    public List<Frame> readBytes(byte[] bytes) {
        List<Frame> output = new ArrayList<>();
        int incomingBytesLength = bytes.length;
        if (incomingBytesLength < 1) {
            return output;
        }

        int readerIdx = 0;

        while (readerIdx < incomingBytesLength) {
            if (pendingIncompleteFrame) {
                if (incomingBytesLength < expectedLength) {
                    System.arraycopy(bytes, 0, byteBuffer, existingLength, incomingBytesLength);
                    pendingIncompleteFrame = true;
                    existingLength += incomingBytesLength;
                    expectedLength -= incomingBytesLength;
                    readerIdx += incomingBytesLength;
                } else {
                    System.arraycopy(bytes, 0, byteBuffer, existingLength, expectedLength);
                    extractOneFrame(byteBuffer, readerIdx, output);
                    readerIdx += expectedLength;
                    pendingIncompleteFrame = false;
                }


            } else {

                int payloadOffset = readerIdx + PAYLOAD_OFFSET;
                int messageLength = bytes[readerIdx + MESSAGE_LENGTH_OFFSET];

                if ((incomingBytesLength - payloadOffset) >= messageLength - 1) {

                    extractOneFrame(bytes, readerIdx, output);
                    readerIdx += messageLength + 1;

                } else {


                    System.arraycopy(bytes, readerIdx, byteBuffer, readerIdx, incomingBytesLength - readerIdx);
                    pendingIncompleteFrame = true;
                    existingLength = incomingBytesLength - readerIdx;
                    expectedLength = messageLength - existingLength + 1;
                    readerIdx = incomingBytesLength;
                }
            }
        }
        return output;

    }


    /**
     * Extract one Frame object when there's enough and complete bytes in the buffer.
     *
     * @param bytes     a complete byte buffer containing one <a{@link Frame}</a> object.
     * @param readerIdx the position of the first byte of the concerning <a{@link Frame}</a> object.
     * @param output    extracted frame is added to the list
     */

    private void extractOneFrame(byte[] bytes, int readerIdx, List<Frame> output) {
        int payloadOffset = readerIdx + PAYLOAD_OFFSET;
        int messageLengthOffset = readerIdx;
        int messageTypeOffset = readerIdx + MESSAGE_TYPE_OFFSET;
        int messageLength = bytes[messageLengthOffset];
        int type = bytes[messageTypeOffset];

        Frame f;

        switch (type) {
            case 1:
                String s = new String(Arrays.copyOfRange(bytes, payloadOffset, payloadOffset + messageLength - 1), StandardCharsets.UTF_8);
                f = new Frame(FrameType.PICK, s);
                output.add(f);
                logger.info("Decoded message: " + f);
                break;
            case 2:
                f = new Frame(FrameType.DROP);
                output.add(f);
                logger.info("Decoded message: " + f);
                break;
            default:
                throw new Error("Unsupported Type of Frame");
        }

    }

}
