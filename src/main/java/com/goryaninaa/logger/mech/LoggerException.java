package com.goryaninaa.logger.mech;

/**
 * This exception will be thrown due to some problem while working with logger
 * functionality.
 *
 * @author Alex Goryanin
 */
public class LoggerException extends RuntimeException {

  private static final long serialVersionUID = 6937214350662675137L;

  public LoggerException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public LoggerException(final String message) {
    super(message);
  }

}
