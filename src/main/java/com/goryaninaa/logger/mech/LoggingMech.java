package com.goryaninaa.logger.mech;

import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is responsible for controlling logging instances and it's
 * properties.
 *
 * @author Alex Goryanin
 */
public final class LoggingMech {

  private static volatile LoggingMech instance; // NOPMD
  private final Queue<String> loggingTaskQueue = new ConcurrentLinkedQueue<>();
  private volatile FileSystemAccess fsa; // NOPMD
  private volatile ExecutorService exec; // NOPMD
  private volatile Level level; // NOPMD

  private LoggingMech() {

  }

  /**
   * According to singleton design pattern returns the only instance of this
   * class.
   *
   * @return - only instance
   */
  public static LoggingMech getInstance() { // NOPMD
    LoggingMech localInstance = instance;
    if (localInstance == null) {
      synchronized (LoggingMech.class) {
        localInstance = instance;
        if (localInstance == null) {
          instance = localInstance = new LoggingMech();
        }
      }
    }
    return localInstance;
  }

  /**
   * Applies passed properties to logger.
   *
   * @param properties - properties of logger
   */
  public void apply(final Properties properties) {
    synchronized (this) {
      this.fsa = new FileSystemAccess(properties);
      setLevel(properties.getProperty("LoggingMech.Level"));
    }
  }

  /**
   * Starts the logging process.
   *
   */
  public void startLogging() {
    synchronized (this) {
      if (fsa != null && level != null) {
        exec = Executors.newSingleThreadExecutor();
        exec.submit(() -> runLog());
      } else {
        throw new LoggerException(
            "Logger doesn't started because it was not initialized correctly");
      }
    }
  }

  /**
   * Stops the logging process.
   *
   */
  public void stopLogging() {
    synchronized (this) {
      if (exec != null) {
        exec.shutdown();
      }
    }
  }

  public static Logger getLogger(final String loggingClassName) {
    return new Logger(loggingClassName, getInstance());
  }

  private void setLevel(final String levelProperty) {
    if (Level.DEBUG.toString().equals(levelProperty)) {
      this.level = Level.DEBUG;
    } else if (Level.INFO.toString().equals(levelProperty)) {
      this.level = Level.INFO;
    } else if (Level.WARN.toString().equals(levelProperty)) {
      this.level = Level.WARN;
    } else {
      throw new IllegalArgumentException("Unsupported LoggingMech.Level propertie");
    }
  }

  /* default */ boolean isDebugLevelLoggingActive() {
    boolean isActive;
    if (level == Level.DEBUG) {
      isActive = true;
    } else {
      isActive = false;
    }
    return isActive;
  }

  /* default */ boolean isInfoLevelLoggingActive() {
    boolean isActive;
    if (level == Level.DEBUG || level == Level.INFO) {
      isActive = true;
    } else {
      isActive = false;
    }
    return isActive;
  }

  /* default */ boolean isWarnLevelLoggingActive() {
    return true;
  }

  /* default */ boolean isErrorLevelLoggingActive() {
    return true;
  }

  /* default */ void submit(final String record) {
    loggingTaskQueue.add(record);
  }

  private void logRecord() {
    final String record = loggingTaskQueue.poll();
    fsa.writeLog(record);
    System.out.println(record); // NOPMD
  }

  private void runLog() {
    while (true) {
      if (!loggingTaskQueue.isEmpty()) {
        logRecord();
      }
    }
  }
}
