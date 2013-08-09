package org.gopivotal.app.util;

import org.springframework.util.StringUtils;

/**
 * The NumberUtils class is a utility class for working with numbers.
 * <p/>
 * @author John Blum
 * @see java.lang.Number
 * @since 7.5
 */
@SuppressWarnings("unused")
public abstract class NumberUtils {

  public static boolean isNumeric(String value) {
    value = String.valueOf(value).trim(); // guard against null

    for (char chr : value.toCharArray()) {
      if (!Character.isDigit(chr)) {
        return false;
      }
    }

    return true;
  }

  public static Long longValue(final Object value) {
    return (value instanceof Number ? ((Number) value).longValue() : parseLong(String.valueOf(value)));
  }

  public static Long parseLong(final String value) {
    try {
      return Long.parseLong(StringUtils.trimAllWhitespace(value));
    }
    catch (NumberFormatException ignore) {
      return null;
    }
  }

}
