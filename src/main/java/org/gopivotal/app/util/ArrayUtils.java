package org.gopivotal.app.util;

/**
 * The ArrayUtils class is an abstract utility class for working with Object arrays.
 * <p/>
 * @author John Blum
 * @see java.util.Arrays
 * @since 7.5
 */
@SuppressWarnings("unused")
public abstract class ArrayUtils {

  public static boolean isEmpty(final Object[] array) {
    return (array == null || array.length == 0);
  }

  public static boolean isNotEmpty(final Object[] array) {
    return !isEmpty(array);
  }

  public static int length(final Object[] array) {
    return (array == null ? 0 : array.length);
  }

  public static String toString(final Object... array) {
    final StringBuilder buffer = new StringBuilder("[");
    int count = 0;

    if (array != null) {
      for (Object element : array) {
        buffer.append(count++ > 0 ? ", " : "").append(String.valueOf(element));
      }
    }

    buffer.append("]");

    return buffer.toString();
  }

}
