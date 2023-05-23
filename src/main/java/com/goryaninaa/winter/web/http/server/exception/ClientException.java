package com.goryaninaa.winter.web.http.server.exception;

import com.goryaninaa.winter.web.http.server.request.handler.HttpResponseCode;

/**
 * Exception that will be thrown due to some client's error in request.
 *
 * @author Alex Goryanin
 */
public class ClientException extends RuntimeException {

  private final HttpResponseCode responseCode;
  private static final long serialVersionUID = 1L;

  public ClientException(final String message, final HttpResponseCode responseCode) {
    super(message);
    this.responseCode = responseCode;
  }

  public HttpResponseCode getResponseCode() {
    return responseCode;
  }
}
