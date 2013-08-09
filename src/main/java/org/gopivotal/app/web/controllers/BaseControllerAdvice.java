package org.gopivotal.app.web.controllers;

import java.util.logging.Logger;

import org.gopivotal.app.util.ExceptionUtils;
import org.gopivotal.app.web.controllers.util.RegionNotFoundException;
import org.gopivotal.app.web.controllers.util.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The CrudControllerAdvice class...
 * <p/>
 * @author John Blum
 * @see org.springframework.web.bind.annotation.ControllerAdvice
 * @since 7.5
 */
@ControllerAdvice
@SuppressWarnings("unused")
public class BaseControllerAdvice {

  protected final Logger logger = Logger.getLogger(getClass().getName());

  /**
   * Handles both ResourceNotFoundExceptions and specifically, RegionNotFoundExceptions, occurring when a resource
   * or a Region (a.k.a. resource) does not exist in GemFire.
   * <p/>
   * @param e the RuntimeException thrown when the accessed/requested resource does not exist in GemFire.
   * @return the String message from the RuntimeException.
   * @see org.gopivotal.app.web.controllers.util.RegionNotFoundException
   * @see org.gopivotal.app.web.controllers.util.ResourceNotFoundException
   * @see org.springframework.web.bind.annotation.ExceptionHandler
   * @see org.springframework.web.bind.annotation.ResponseBody
   * @see org.springframework.web.bind.annotation.ResponseStatus
   */
  @ExceptionHandler({ RegionNotFoundException.class, ResourceNotFoundException.class })
  @ResponseBody
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public String handle(final RuntimeException e) {
    return e.getMessage();
  }

  /**
   * Handles any Exception thrown by a REST API web service endpoint, HTTP request handler method.
   * <p/>
   * @param cause the Exception causing the error.
   * @return a ResponseEntity with an appropriate HTTP status code (500 - Internal Server Error) and HTTP response body
   * containing the stack trace of the Exception.
   * @see java.lang.Exception
   * @see org.springframework.web.bind.annotation.ExceptionHandler
   * @see org.springframework.web.bind.annotation.ResponseBody
   * @see org.springframework.web.bind.annotation.ResponseStatus
   */
  @ExceptionHandler(Throwable.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleException(final Throwable cause) {
    final String stackTrace = ExceptionUtils.printStackTrace(cause);
    logger.severe(stackTrace);
    return stackTrace;
  }

}
