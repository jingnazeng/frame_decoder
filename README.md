# Frame Decoder

This repository contains source code for a frame decoder follow the protocol specification below.

## Protocol Specification

This is a binary protocol which is capable of transmitting two types of
messages. Each protocol frame is a sequence of bytes consisting of the frame
length, an identifier which corresponds to the message type, and an optional
message body:

    >-- increasing byte index -->
    +-----------------+-----------------+------------------------+
    | FrameLength (1) | MessageType (1) | Body (FrameLength - 1) |
    +-----------------+-----------------+------------------------+

`FrameLength` is a 1 byte signed integer, (with a max value of 126) that
represents the length of the data to follow (`MessageType` + `Body`).
`MessageType` is a 1 byte signed integer (with a max value of 126) which
indicates the type of message. `Body` contains the body of the message, if
present.



## Run using Maven
mvn test: it will compile the code of your application and your tests. It will then run the test and let you know if some fails.
mvn clean install: it will do everything mvn test does and then if everything looks file it will install the library or the application into your local maven repository (typically under /.m2). In this way you could use this library from other projects you want to build on the same machine.


## Test Output

Examples:

 [main] INFO  c.frame.decoder.MessageFrameDecoder - Decoded message: PICK(foo)
 [1/9] PASSED "Both messages arrive in one single chunk".
 23:45:57.829 [main] INFO  c.frame.decoder.MessageFrameDecoder - Decoded message: DROP
 [1/9] PASSED "Both messages arrive in one single chunk".
 [2/9] PASSED "A Pick message arrives in two chunks".
 23:45:57.830 [main] INFO  c.frame.decoder.MessageFrameDecoder - Decoded message: PICK(me)
 ...
 

## MISC

Some additional things that would have been nice to have
- possibility to signal a "Message corrupted" warning.
- possibility to test out failure mode.



