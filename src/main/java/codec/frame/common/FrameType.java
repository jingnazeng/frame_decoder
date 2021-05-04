package codec.frame.common;

public enum FrameType implements NumericEnum {

    PICK {
        @Override
        public long getValue() {
            return 1;
        }
    },
    DROP {
        @Override
        public long getValue() {
            return 2;
        }
    },

}




