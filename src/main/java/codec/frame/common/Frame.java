package codec.frame.common;

public class Frame {

    private FrameType frameType;
    private String message;

    public Frame(FrameType frameType){
        this.frameType = frameType;

    }

    public Frame(FrameType frameType, String message){
        this.frameType = frameType;
        this.message = message;

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final Frame other = (Frame) obj;
        if ((this.message == null) ? (other.message != null) : !this.message.equals(other.message)) {
            return false;
        }

        if (this.frameType != other.frameType) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.frameType != null ? this.frameType.hashCode() : 0);
        hash = 53 * hash + this.message.hashCode();
        return hash;
    }

    @Override
    public String toString(){
        return this.frameType + ((this.message == null) ? "" : "(" + this.message + ")");
    }
}
