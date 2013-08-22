package org.gopivotal.app.tests;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.junit.Test;

/**
 * The URLEncoderDecoderTests class is a test suite of test cases testing the contract and functionality
 * of the URLEncoder and URLDecoder classes.
 * <p/>
 * @author John Blum
 * @see java.net.URLDecoder
 * @see java.net.URLEncoder
 * @since 7.5
 */
@SuppressWarnings("unused")
public class URLEncoderDecoderTests {

  private static final String UTF_8 = "UTF-8";
  private static final String DEFAULT_ENCODING = UTF_8;

  protected static String decode(final String value) {
    return decode(value, DEFAULT_ENCODING);
  }

  protected static String decode(final String value, final String encoding) {
    try {
      return URLDecoder.decode(value, encoding);
    }
    catch (UnsupportedEncodingException e) {
      System.err.printf("An unsupported encoding exception thrown while decoding: %1$s", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  protected static String encode(final String value) {
    return encode(value, DEFAULT_ENCODING);
  }

  protected static String encode(final String value, final String encoding) {
    try {
      return URLEncoder.encode(value,encoding);
    }
    catch (UnsupportedEncodingException e) {
      System.err.printf("An unsupported encoding exception thrown while encoding: %1$s", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testEncodingDecoding() {
    assertEquals("People%2FAddress", encode("People/Address"));
    assertEquals("People/Address", decode("People%2FAddress"));
    assertEquals("People/Address", decode(decode(decode(decode("People%25252FAddress")))));
  }

}
