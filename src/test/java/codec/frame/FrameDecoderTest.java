package codec.frame;// Run with: $ javac *.java && java FrameDecoderTest

import codec.frame.common.Frame;
import codec.frame.common.FrameType;
import codec.frame.decoder.FrameDecoder;
import codec.frame.decoder.MessageFrameDecoder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrameDecoderTest {

    public static class Test {
        public final TestData[] testData;
        public final String name;

        public static Test make(String name, TestData... data) {
            return new Test(name, data);
        }

        private Test(String name, TestData[] testData) {
            this.name = name;
            this.testData = testData;
        }
    }

    public static class TestData {
        public byte[] bytes;
        public List<Frame> expectedOutput;

        public TestData(byte[] bytes, List<Frame> output) {
            this.bytes = bytes;
            this.expectedOutput = output;
        }
    }

    public static Test[] Tests = new Test[]{
            Test.make(
                    "Both messages arrive in one single chunk",
                    new TestData(new byte[]{0x04, 0x01, 'f', 'o', 'o'}, new ArrayList<Frame>(Arrays.asList(new Frame(FrameType.PICK, "foo")))),
                    new TestData(new byte[]{0x01, 0x02}, new ArrayList<Frame>(Arrays.asList(new Frame(FrameType.DROP))))
            ),

            Test.make(
                    "A Pick message arrives in two chunks",
                    new TestData(new byte[]{0x03, 0x01}, new ArrayList<Frame>()),
                    new TestData(new byte[]{'m', 'e'}, new ArrayList<Frame>(Arrays.asList(new Frame(FrameType.PICK, "me"))))
            ),

            Test.make(
                    "An empty byte array",
                    new TestData(new byte[]{}, new ArrayList<Frame>())
            ),

            Test.make(
                    "Two Drop messages arrive at once",
                    new TestData(new byte[]{0x01, 0x02, 0x01, 0x02}, new ArrayList<Frame>(Arrays.asList(new Frame(FrameType.DROP), new Frame(FrameType.DROP))))
            ),
            Test.make(
                    "Many messages arrive at once",
                    new TestData(new byte[]{0x01, 0x02, 0x01, 0x02, 0x04, 0x01, 'b', 'a', 'r'}, new ArrayList<Frame>(Arrays.asList(new Frame(FrameType.DROP), new Frame(FrameType.DROP), new Frame(FrameType.PICK, "bar"))))
            ),
            Test.make(
                    "A Pick message arrives in two chunks",
                    new TestData(new byte[]{0x06, 0x01, 'b', 'a', 'r'}, new ArrayList<Frame>()),
                    new TestData(new byte[]{'m', 'e'}, new ArrayList<Frame>(Arrays.asList(new Frame(FrameType.PICK, "barme"))))
            ),
            Test.make(
                    "A Pick and a Drop message arrives in two chunks",
                    new TestData(new byte[]{0x06, 0x01, 'm', 'o', 'n'}, new ArrayList<Frame>()),
                    new TestData(new byte[]{'d', 'l', 0x01, 0x02}, new ArrayList<Frame>(Arrays.asList(new Frame(FrameType.PICK, "mondl"), new Frame(FrameType.DROP))))
            ),
            Test.make(
                    "A Pick and a Drop message arrives in three chunks",
                    new TestData(new byte[]{0x07, 0x01, 'm', 'o', 'n'}, new ArrayList<Frame>()),
                    new TestData(new byte[]{'d', 'l'}, new ArrayList<Frame>()),
                    new TestData(new byte[]{'e'}, new ArrayList<Frame>(Arrays.asList(new Frame(FrameType.PICK, "mondle"))))
            ),
            Test.make(
                    "A  Drop message arrives in two chunks, each with a single byte",
                    new TestData(new byte[]{0x01}, new ArrayList<Frame>()),
                    new TestData(new byte[]{0x02}, new ArrayList<Frame>(Arrays.asList(new Frame(FrameType.DROP))))
            ),


    };


//    public static void main(String[] args) {
    @org.junit.jupiter.api.Test
    void messsagesFrameDecoderTest(){
        int numFailures = 0;
        int testsRun = 0;
        int totalTests = Tests.length;
        System.out.println("Running " + totalTests + " tests");

        for (Test test : Tests) {
            FrameDecoder decoder = new MessageFrameDecoder();
            testsRun++;
            for (TestData data : test.testData) {
                List<Frame> result = decoder.readBytes(data.bytes);
                if (result.equals(data.expectedOutput)) {
                    System.out.println(
                            "[" + testsRun + "/" + totalTests + "] PASSED \"" + test.name + "\".");
                } else {
                    System.err.println(
                            "[" + testsRun + "/" + totalTests + "] *FAILED* \"" + test.name + "\". " +
                                    "Message: Expected " + data.expectedOutput + " but found " + result + ".");
                    numFailures++;
                    break;
                }
            }
        }

        if (numFailures > 0) {
            System.err.println("Tests run: " + testsRun + ", Failures: " + numFailures);
            System.exit(1);
        } else {
            System.out.println("OK (" + testsRun + " tests)");
        }
    }
}
