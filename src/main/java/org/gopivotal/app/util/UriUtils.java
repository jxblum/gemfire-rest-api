package org.gopivotal.app.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * The UriUtils is a utility class for working with URIs and URLs.
 * <p/>
 * @author John Blum
 * @see java.net.URI
 * @see java.net.URL
 * @see java.net.URLDecoder
 * @see java.net.URLEncoder
 * @since 7.5
 */
@SuppressWarnings("unused")
public abstract class UriUtils {

  public static final String UTF_8 = "UTF-8";
  public static final String DEFAULT_ENCODING = UTF_8;

  private static final boolean DEFAULT_THROW_ON_UNSUPPORTED_ENCODING = true;

  public static String[] decode(final String[] values) {
    return decode(values, DEFAULT_ENCODING, DEFAULT_THROW_ON_UNSUPPORTED_ENCODING);
  }

  public static String[] decode(final String[] values, final boolean throwOnUnsupportedEncoding) {
    return decode(values, DEFAULT_ENCODING, throwOnUnsupportedEncoding);
  }

  public static String[] decode(final String[] values, final String encoding) {
    return decode(values, encoding, DEFAULT_THROW_ON_UNSUPPORTED_ENCODING);
  }

  public static String[] decode(final String[] values, final String encoding, final boolean throwForUnsupportedEncoding) {
    if (values != null) {
      for (int index = 0; index < values.length; index++) {
        values[index] = decode(values[index], encoding, throwForUnsupportedEncoding);
      }
    }

    return values;
  }

  public static String decode(final String value) {
    return decode(value, DEFAULT_ENCODING, DEFAULT_THROW_ON_UNSUPPORTED_ENCODING);
  }

  public static String decode(final String value, final boolean throwOnUnsupportedEncoding) {
    return decode(value, DEFAULT_ENCODING, throwOnUnsupportedEncoding);
  }

  public static String decode(final String value, final String encoding) {
    return decode(value, encoding, DEFAULT_THROW_ON_UNSUPPORTED_ENCODING);
  }

  public static String decode(String value, final String encoding, final boolean throwOnUnsupportedEncoding) {
    try {
      String previousValue;

      do {
        previousValue = value;
        value = URLDecoder.decode(value, encoding);
      }
      while (!value.equals(previousValue));

      return value;
    }
    catch (UnsupportedEncodingException e) {
      if (throwOnUnsupportedEncoding) {
        throw new RuntimeException(e);
      }
      return value;
    }
  }

  public static String[] encode(final String[] values) {
    return encode(values, DEFAULT_ENCODING, DEFAULT_THROW_ON_UNSUPPORTED_ENCODING);
  }

  public static String[] encode(final String[] values, final boolean throwForUnsupportedEncoding) {
    return encode(values, DEFAULT_ENCODING, throwForUnsupportedEncoding);
  }

  public static String[] encode(final String[] values, final String encoding) {
    return encode(values, encoding, DEFAULT_THROW_ON_UNSUPPORTED_ENCODING);
  }

  public static String[] encode(final String[] values, final String encoding, final boolean throwForUnsupportedEncoding) {
    if (values != null) {
      for (int index = 0; index < values.length; index++) {
        values[index] = encode(values[index], encoding, throwForUnsupportedEncoding);
      }
    }

    return values;
  }

  public static String encode(final String value) {
    return encode(value, DEFAULT_ENCODING, DEFAULT_THROW_ON_UNSUPPORTED_ENCODING);
  }

  public static String encode(final String value, final boolean throwForUnsupportedEncoding) {
    return encode(value, DEFAULT_ENCODING, throwForUnsupportedEncoding);
  }

  public static String encode(final String value, final String encoding) {
    return encode(value, encoding, DEFAULT_THROW_ON_UNSUPPORTED_ENCODING);
  }

  public static String encode(final String value, final String encoding, final boolean throwForUnsupportedEncoding) {
    try {
      return URLEncoder.encode(value, encoding);
    }
    catch (UnsupportedEncodingException e) {
      if (throwForUnsupportedEncoding) {
        throw new RuntimeException(e);
      }
      return value;
    }
  }

}
