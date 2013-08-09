package org.gopivotal.app.util;

/**
 * The ValidationUtils class is a utility class for performing validations.
 * <p/>
 * @author John Blum
 * @since 7.5
 */
@SuppressWarnings("unused")
public abstract class ValidationUtils {

  public static <T> T returnValueThrowOnNull(final T value, final String message, final Object... args) {
    return returnValueThrowOnNull(value, new NullPointerException(String.format(message, args)));
  }

  public static <T> T returnValueThrowOnNull(final T value, final RuntimeException e) {
    if (value == null) {
      throw e;
    }

    return value;
  }

}
