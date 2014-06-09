package org.gopivotal.app.web.controllers.util;

/**
 * The RegionNotFoundException class is a RuntimeException indicating that a Region could not be found in the Cache.
 * <p/>
 * @author John Blum
 * @see org.gopivotal.app.web.controllers.util.ResourceNotFoundException
 * @since 7.5
 */
@SuppressWarnings("unused")
public class RegionNotFoundException extends ResourceNotFoundException {

  public RegionNotFoundException() {
  }

  public RegionNotFoundException(final String message) {
    super(message);
  }

  public RegionNotFoundException(final Throwable cause) {
    super(cause);
  }

  public RegionNotFoundException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
