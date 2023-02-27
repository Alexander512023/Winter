package com.goryaninaa.logger.mech;

import static org.junit.Assert.assertTrue;

import com.goryaninaa.logger.LoggingMech.Logger;
import com.goryaninaa.logger.LoggingMech.LoggingMech;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Before;
import org.junit.Test;

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

  @Test
  public void loggerShouldWriteCorrectLogMessageToFs() throws InterruptedException, IOException {
    writeLogMessageToLogFileOnFs();
    final String message = readWrittenMessageFromLogFileOnFs();
    deleteLogFile();
    final Matcher matcher = createMatcherToAssert(message);
    assertTrue(matcher.find());
  }

  private Matcher createMatcherToAssert(final String message) {
    final Pattern pattern = Pattern.compile(
            "\\{\"localDateTime\":\".+?\"," + "\"thread\":\"main\"," + "\"level\":\"ERROR\","
            + "\"loggerName\":\"com.goryaninaa.logger.mech.LoggerTest\","
            + "\"message\":\"Test\"\\}");
    return pattern.matcher(message);
  }

  private void deleteLogFile() {
    new File(PATH + "/" + new File(PATH).list()[0]).delete();
  }

  private String readWrittenMessageFromLogFileOnFs() throws FileNotFoundException, IOException {
    try (BufferedReader reader = Files
        .newBufferedReader(Paths.get(PATH + "/" + new File(PATH).list()[0]))) {
      return reader.readLine();
    }
  }

  private void writeLogMessageToLogFileOnFs() throws InterruptedException {
    LoggingMech.getInstance().apply(properties);
    LoggingMech.getInstance().startLogging();
    final Logger logger = LoggingMech.getLogger(this.getClass().getCanonicalName());
    logger.error("Test");
    TimeUnit.MILLISECONDS.sleep(10);
    LoggingMech.getInstance().stopLogging();
  }

}
