package com.goryaninaa.winter.logger.mech;

import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is responsible for controlling logging instances and it's
 * properties.
 *
 * @author Alex Goryanin
 */
@SuppressWarnings("SameReturnValue")
public final class LoggingMech {

  private static LoggingMech instance;
  private final AtomicBoolean logging;
  private final Queue<String> loggingTaskQueue = new ConcurrentLinkedQueue<>();
  private FileSystemAccess fsa;
  private ExecutorService exec;
  private Level level;

  private LoggingMech() {
    logging = new AtomicBoolean(false);
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
      logging.set(true);
      if (fsa != null && level != null) {
        exec = Executors.newSingleThreadExecutor();
        exec.submit(this::runLog);
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
      logging.set(false);
      if (exec != null) {
        exec.shutdown();
      }
    }
  }

  public static Logger getLogger(final String loggingClassName) {
    return new Logger(loggingClassName, getInstance());
  }

  public Level getLevel() {
    return level;
  }

  private void setLevel(final String levelProperty) {
    if (Level.DEBUG.toString().equals(levelProperty)) {
      this.level = Level.DEBUG;
    } else if (Level.INFO.toString().equals(levelProperty)) {
      this.level = Level.INFO;
    } else if (Level.WARN.toString().equals(levelProperty)) {
      this.level = Level.WARN;
    } else if (Level.ERROR.toString().equals(levelProperty)) {
      this.level = Level.ERROR;
    } else {
      throw new IllegalArgumentException("Unsupported LoggingMech.Level property");
    }
  }

  /* default */ void submit(final String logRecord) {
    loggingTaskQueue.add(logRecord);
  }

  private void logRecord() {
    final String logRecord = loggingTaskQueue.poll();
    fsa.writeLog(logRecord);
    System.out.println(logRecord);
  }

  private void runLog() {
    while (logging.get()) {
      if (!loggingTaskQueue.isEmpty()) {
        logRecord();
      }
    }
  }
}
