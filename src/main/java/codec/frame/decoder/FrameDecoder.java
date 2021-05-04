package codec.frame.decoder;

import codec.frame.common.Frame;

import java.util.List;


public interface FrameDecoder {
  /**
   * Returns zero or more fully buffered  <a{@link Frame}</a>  by reading `bytes` as they
   * arrive and decoding any complete frames.
   *
   * @param bytes the input bytes containing zero or more frames.
   *
   * @return zero or more decoded frames.
   */
  public List<Frame> readBytes(byte[] bytes);


}
