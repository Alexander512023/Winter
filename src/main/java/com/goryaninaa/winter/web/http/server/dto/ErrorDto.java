package com.goryaninaa.winter.web.http.server.dto;

/**
 * Send this object if request handling was finished with an error.
 */
@SuppressWarnings("unused")
public class ErrorDto {

  private final int code;
  private final String message;

  /**
   * Constructor method. Takes code and message as a parameters.
   *
   * @param code - code of error
   * @param message - text of error
   */
  public ErrorDto(final int code, final String message) {
    super();
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
