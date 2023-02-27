package com.goryaninaa.logger.mech;

import java.time.LocalDateTime;

/**
 * Instances of this class should be created in classes which would be logged.
 *
 * @author Alex Goryanin
 */
public class Logger {

  private final String loggingClassName;
  private final LoggingMech loggingMech;

  public Logger(final String loggingClassName, final LoggingMech loggingMech) {
    this.loggingClassName = loggingClassName;
    this.loggingMech = loggingMech;
  }

  
  /**
   * Generate log record of level error.
   *
   * @param message - message which you want to include to log record
   */
  public void error(final String message) {
    final String record = generateErrorLogRecord(message);
    loggingMech.submit(record);
  }

  /**
   * Generate log record of level warn.
   *
   * @param message - message which you want to include to log record
   */
  public void warn(final String message) {
    final String record = generateWarnLogRecord(message);
    loggingMech.submit(record);
  }

  /**
   * Generate log record of level info.
   *
   * @param message - message which you want to include to log record
   */
  public void info(final String message) {
    if (loggingMech.isInfoLevelLoggingActive()) {
      final String record = generateInfoLogRecord(message);
      loggingMech.submit(record);
    }
  }

  /**
   * Generate log record of level debug.
   *
   * @param message - message which you want to include to log record
   */
  public void debug(final String message) {
    if (loggingMech.isDebugLevelLoggingActive()) {
      final String record = generateDebugLogRecord(message);
      loggingMech.submit(record);
    }
  }
  
  public boolean isErrorEnabled() {
    return loggingMech.isErrorLevelLoggingActive();
  }

  public boolean isWarnEnabled() {
    return loggingMech.isWarnLevelLoggingActive();
  }

  public boolean isInfoEnabled() {
    return loggingMech.isInfoLevelLoggingActive();
  }

  public boolean isDebugEnabled() {
    return loggingMech.isDebugLevelLoggingActive();
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
    return "{\"localDateTime\":\"" + LocalDateTime.now().toString() + "\"," // NOPMD
        + "\"thread\":\"" + Thread.currentThread().getName() + "\"," + "\"level\":\"" + level
        + "\"," + "\"loggerName\":\"" + loggingClassName + "\"," + "\"message\":" + "\"" + message
        + "\"}";
  }
}
