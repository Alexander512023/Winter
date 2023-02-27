package com.goryaninaa.web.http.server.exception;

/**
 * Exception that will be thrown due to some server error during request
 * handling.
 *
 * @author Alex Goryanin
 */
public class ServerException extends RuntimeException {

  private static final long serialVersionUID = 6932562485276829015L;

  public ServerException(final String message) {
    super(message);
  }

  public ServerException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
