package org.gopivotal.app.web.controllers.util;

/**
 * The ResourceNotFoundException class is a RuntimeException indicating that the targeted resource could not be located.
 * <p/>
 * @author John Blum
 * @see java.lang.RuntimeException
 * @since 7.5
 */
@SuppressWarnings("unused")
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException() {
  }

  public ResourceNotFoundException(final String message) {
    super(message);
  }

  public ResourceNotFoundException(final Throwable cause) {
    super(cause);
  }

  public ResourceNotFoundException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
