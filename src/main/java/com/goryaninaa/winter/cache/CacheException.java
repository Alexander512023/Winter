package com.goryaninaa.winter.cache;

/**
 * This exception will be thrown due to some problem while working with cache
 * functionality.
 *
 * @author Alex Goryanin
 */
public class CacheException extends RuntimeException {

  private static final long serialVersionUID = -3835989779533432304L;

  public CacheException(final String message, final Throwable cause) {
    super(message, cause);
  }

  @SuppressWarnings("unused")
  public CacheException(final String message) {
    super(message);
  }

}
