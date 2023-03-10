package com.goryaninaa.winter.logger.mech;

import java.time.LocalDateTime;

/**
 * Instances of this class should be created in classes which would be logged.
 *
 * @author Alex Goryanin
 */
public class Logger {

  private final String loggingClassName;
  private final LoggingMech loggingMech;

  /* default */ Logger(final String loggingClassName, final LoggingMech loggingMech) {
    this.loggingClassName = loggingClassName;
    this.loggingMech = loggingMech;
  }

  
  /**
   * Generate log record of level error.
   *
   * @param message - message which you want to include to log record
   */
  public void error(final String message) {
    if (isErrorEnabled()) {
      final String logRecord = generateErrorLogRecord(message);
      loggingMech.submit(logRecord);
    }
  }

  /**
   * Generate log record of level warn.
   *
   * @param message - message which you want to include to log record
   */
  public void warn(final String message) {
    if (isWarnEnabled()) {
      final String logRecord = generateWarnLogRecord(message);
      loggingMech.submit(logRecord);
    }
  }

  /**
   * Generate log record of level info.
   *
   * @param message - message which you want to include to log record
   */
  public void info(final String message) {
    if (isInfoEnabled()) {
      final String logRecord = generateInfoLogRecord(message);
      loggingMech.submit(logRecord);
    }
  }

  /**
   * Generate log record of level debug.
   *
   * @param message - message which you want to include to log record
   */
  public void debug(final String message) {
    if (isDebugEnabled()) {
      final String logRecord = generateDebugLogRecord(message);
      loggingMech.submit(logRecord);
    }
  }

  /**
   * Use this method to check, before logging.
   *
   * @return true if error level of logging enabled on the opposite - false.
   */
  public boolean isErrorEnabled() {
    Level level = loggingMech.getLevel();
    return level == Level.DEBUG
            || level == Level.INFO || level == Level.WARN || level == Level.ERROR;
  }

  /**
   * Use this method to check, before logging.
   *
   * @return true if warn level of logging enabled on the opposite - false.
   */
  public boolean isWarnEnabled() {
    Level level = loggingMech.getLevel();
    return level == Level.DEBUG || level == Level.INFO || level == Level.WARN;
  }

  /**
   * Use this method to check, before logging.
   *
   * @return true if info level of logging enabled on the opposite - false.
   */
  public boolean isInfoEnabled() {
    Level level = loggingMech.getLevel();
    return level == Level.DEBUG || level == Level.INFO;
  }

  /**
   * Use this method to check, before logging.
   *
   * @return true if debug level of logging enabled on the opposite - false.
   */
  public boolean isDebugEnabled() {
    Level level = loggingMech.getLevel();
    return level == Level.DEBUG;
  }

  private String generateErrorLogRecord(final String message) {
    return generateLogRecord(message, "ERROR");
  }

  private String generateWarnLogRecord(final String message) {
    return generateLogRecord(message, "WARN");
  }

  private String generateInfoLogRecord(final String message) {
    return generateLogRecord(message, "INFO");
  }

  private String generateDebugLogRecord(final String message) {
    return generateLogRecord(message, "DEBUG");
  }

  private String generateLogRecord(final String message, final String level) {
    return "{\"localDateTime\":\"" + LocalDateTime.now() + "\"," // NOPMD
        + "\"thread\":\"" + Thread.currentThread().getName() + "\"," + "\"level\":\"" + level
        + "\"," + "\"loggerName\":\"" + loggingClassName + "\"," + "\"message\":" + "\"" + message
        + "\"}";
  }
}
