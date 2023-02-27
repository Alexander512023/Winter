package com.goryaninaa.web.http.server.exception;

/**
 * Exception that will be thrown due to some client's error in request.
 *
 * @author Alex Goryanin
 */
public class ClientException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ClientException(final String message) {
    super(message);
  }
}
