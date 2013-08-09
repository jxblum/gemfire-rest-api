package org.gopivotal.app.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * The ExceptionUtils class is a utility class for working with Throwables (both checked and unchecked Exceptions)
 * as well as Errors.
 * <p/>
 * @author John Blum
 * @see java.lang.Throwable
 * @since 7.5
 */
@SuppressWarnings("unused")
public abstract class ExceptionUtils {

  /**
   * Writes the stack trace of the Throwable to a String.
   * <p/>
   * @param t a Throwable object who's stack trace will be written to a String.
   * @return a String containing the stack trace of the Throwable.
   * @see java.io.StringWriter
   * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
   */
  public static String printStackTrace(final Throwable t) {
    final StringWriter stackTraceWriter = new StringWriter();
    t.printStackTrace(new PrintWriter(stackTraceWriter));
    return stackTraceWriter.toString();
  }

}
