package com.goryaninaa.winter.logger.mech;

import com.goryaninaa.winter.Waitility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

/**
 * JUnit test case for {@link Logger}.
 *
 * @author Alex Goryanin
 */
public class LoggerTest {

  private static final  String PATH = "temp/test/logs";
  private final Properties properties = new Properties();

  /**
   * Test utility.
   *
   */
  @Before
  public void initProperties() {
    properties.setProperty("LoggingMech.logsDirPathUrl", PATH);
    properties.setProperty("LoggingMech.bytesPerFile", "10000");
    properties.setProperty("LoggingMech.amountOfLogs", "1");
    properties.setProperty("LoggingMech.Level", "DEBUG");
  }

  @After
  @Before
  public void deleteLogFile() {
    final String[] logFilesNames = new File(PATH).list();
    if (logFilesNames != null) {
      for (final String logFileName : logFilesNames) {
        new File(PATH + "/" + logFileName).delete();
      }
    }
  }

  @Test
  public void loggerShouldWriteCorrectLogMessageToFs() throws InterruptedException, IOException {
    writeLogMessageToLogFileOnFs(Level.ERROR);
    Waitility.waitExecution(this, 25);
    final String message = readWrittenMessageFromLogFileOnFs();
    final Matcher matcher = createMatcherToAssert(message,
            "\\{\"localDateTime\":\".+?\"," + "\"thread\":\"main\"," + "\"level\":\"ERROR\","
                    + "\"loggerName\":\"com.goryaninaa.winter.logger.mech.LoggerTest\","
                    + "\"message\":\"Test\"}");
    assertTrue(matcher.find());
  }

  @Test
  public void error() throws InterruptedException, IOException {
    writeLogMessageToLogFileOnFs(Level.ERROR);
    Waitility.waitExecution(this, 25);
    final String message = readWrittenMessageFromLogFileOnFs();
    final Matcher matcher = createMatcherToAssert(message, "ERROR");
    assertTrue(matcher.find());
  }

  @Test
  public void warn() throws InterruptedException, IOException {
    writeLogMessageToLogFileOnFs(Level.WARN);
    Waitility.waitExecution(this, 25);
    final String message = readWrittenMessageFromLogFileOnFs();
    final Matcher matcher = createMatcherToAssert(message, "WARN");
    assertTrue(matcher.find());
  }

  @Test
  public void info() throws InterruptedException, IOException {
    writeLogMessageToLogFileOnFs(Level.INFO);
    Waitility.waitExecution(this, 25);
    final String message = readWrittenMessageFromLogFileOnFs();
    final Matcher matcher = createMatcherToAssert(message, "INFO");
    assertTrue(matcher.find());
  }

  @Test
  public void debug() throws InterruptedException, IOException {
    writeLogMessageToLogFileOnFs(Level.DEBUG);
    Waitility.waitExecution(this, 25);
    final String message = readWrittenMessageFromLogFileOnFs();
    final Matcher matcher = createMatcherToAssert(message, "DEBUG");
    assertTrue(matcher.find());
  }

  @Test
  public void isErrorEnabled() {
    final boolean errorActiveWhenError = defineState(Level.ERROR, Level.ERROR);
    final boolean errorActiveWhenWarn = defineState(Level.ERROR, Level.WARN);
    final boolean errorActiveWhenInfo = defineState(Level.ERROR, Level.INFO);
    final boolean errorActiveWhenDebug = defineState(Level.ERROR, Level.DEBUG);
    assertTrue(errorActiveWhenError && errorActiveWhenWarn && errorActiveWhenInfo
            && errorActiveWhenDebug);
  }

  @Test
  public void isWarnEnabled() {
    final boolean warnDeActiveWhenError = !defineState(Level.WARN, Level.ERROR);
    final boolean warnActiveWhenWarn = defineState(Level.WARN, Level.WARN);
    final boolean warnActiveWhenInfo = defineState(Level.WARN, Level.INFO);
    final boolean warnActiveWhenDebug = defineState(Level.WARN, Level.DEBUG);
    assertTrue(warnDeActiveWhenError && warnActiveWhenWarn && warnActiveWhenInfo
            && warnActiveWhenDebug);
  }

  @Test
  public void isInfoEnabled() {
    final boolean infoDeActiveWhenError = !defineState(Level.INFO, Level.ERROR);
    final boolean infoDeActiveWhenWarn = !defineState(Level.INFO, Level.WARN);
    final boolean infoActiveWhenInfo = defineState(Level.INFO, Level.INFO);
    final boolean infoActiveWhenDebug = defineState(Level.INFO, Level.DEBUG);
    assertTrue(infoDeActiveWhenError && infoDeActiveWhenWarn && infoActiveWhenInfo
            && infoActiveWhenDebug);
  }

  @Test
  public void isDebugEnabled() {
    final boolean debugDeActiveWhenError = !defineState(Level.DEBUG, Level.ERROR);
    final boolean debugDeActiveWhenWarn = !defineState(Level.DEBUG, Level.WARN);
    final boolean debugDeActiveWhenInfo = !defineState(Level.DEBUG, Level.INFO);
    final boolean debugActiveWhenDebug = defineState(Level.DEBUG, Level.DEBUG);
    assertTrue(debugDeActiveWhenError && debugDeActiveWhenWarn && debugDeActiveWhenInfo
            && debugActiveWhenDebug);
  }

  private boolean defineState(Level verifiable, Level setted) {
    properties.setProperty("LoggingMech.Level", setted.toString());
    LoggingMech.getInstance().apply(properties);
    return check(verifiable);
  }

  private boolean check(Level verifiable) {
    switch (verifiable) {
      case DEBUG: {
        return LoggingMech.getLogger(LoggerTest.class.getCanonicalName()).isDebugEnabled();
      }
      case INFO: {
        return LoggingMech.getLogger(LoggerTest.class.getCanonicalName()).isInfoEnabled();
      }
      case WARN: {
        return LoggingMech.getLogger(LoggerTest.class.getCanonicalName()).isWarnEnabled();
      }
      case ERROR: {
        return LoggingMech.getLogger(LoggerTest.class.getCanonicalName()).isErrorEnabled();
      }
    }
    return false;
  }

  private Matcher createMatcherToAssert(final String message, final String regex) {
    final Pattern pattern = Pattern.compile(regex);
    return pattern.matcher(message);
  }

  private String readWrittenMessageFromLogFileOnFs() throws IOException {
    try (BufferedReader reader = Files
        .newBufferedReader(Paths.get(PATH + "/" + new File(PATH).list()[0]))) {
      return reader.readLine();
    }
  }

  private void writeLogMessageToLogFileOnFs(Level level) throws InterruptedException {
    LoggingMech.getInstance().apply(properties);
    LoggingMech.getInstance().startLogging();
    final Logger logger = LoggingMech.getLogger(this.getClass().getCanonicalName());
    log(logger, level);
    Waitility.waitExecution(this, 40);
    LoggingMech.getInstance().stopLogging();
  }

  private void log(Logger logger, Level level) {
    switch (level) {
      case DEBUG: {
        logger.debug("Test");
        break;
      }
      case INFO: {
        logger.info("Test");
        break;
      }
      case WARN: {
        logger.warn("Test");
        break;
      }
      case ERROR: {
        logger.error("Test");
        break;
      }
    }
  }

}
