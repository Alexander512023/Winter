package com.goryaninaa.winter.logger.mech;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * JUnit test case for {@link FileSystemAccess}.
 *
 * @author Alex Goryanin
 */
@SuppressWarnings("DataFlowIssue")
public class FileSystemAccessTest {

  private static final String PATH = "temp/test/logs";
  private static final String AMOUNT = "Winter.LoggingMech.amountOfLogs";
  private static final String HELLO = "Hello";
  private final Properties properties = new Properties();

  @Before
  public void initProperties() {
    properties.setProperty("Winter.LoggingMech.logsDirPathUrl", PATH);
    properties.setProperty("Winter.LoggingMech.bytesPerFile", "100");
  }

  @After
  @Before
  public void deleteLogFile() {
    deleteDir(new File("temp"));
  }
  @Test
  public void fsaShouldWriteMessageToLogFile() throws IOException {
    properties.setProperty(AMOUNT, "1");
    final FileSystemAccess fsa = new FileSystemAccess(properties);
    fsa.writeLog(HELLO);
    final String actual = readMessageFromLogFileOnFs();
    assertEquals(HELLO, actual);
  }

  @Test
  public void fsaShouldCreateLogFileIfThereIsNoOne() {
    properties.setProperty(AMOUNT, "1");
    final FileSystemAccess fsa = new FileSystemAccess(properties);
    final boolean beforeZero = !(new File(PATH).exists());
    fsa.writeLog(HELLO);
    final boolean afterOne = new File(PATH).list().length == 1;
    assertTrue(beforeZero && afterOne);
  }

  @Test
  public void fsaShouldCreateNewLogFileIfCurrentHasLengthMoreThan100()
          throws InterruptedException {
    properties.setProperty(AMOUNT, "10");
    final FileSystemAccess fsa = new FileSystemAccess(properties);
    final boolean beforeZero = !(new File(PATH).exists());
    for (int i = 0; i < 27; i++) {
      fsa.writeLog(HELLO + i);
    }
    synchronized (this) {
      wait(50);
    }
    final boolean afterTwo = new File(PATH).list().length == 2;
    assertTrue(beforeZero && afterTwo);
  }

  @Test
  public void fsaShouldKeepAmountOfLogFilesAccordingToParameter() {
    properties.setProperty(AMOUNT, "2");
    final FileSystemAccess fsa = new FileSystemAccess(properties);
    final boolean beforeZero = !(new File(PATH).exists());
    for (int i = 0; i < 100; i++) {
      fsa.writeLog(HELLO + i);
    }
    final boolean afterTwo = new File(PATH).list().length == 2;
    assertTrue(beforeZero && afterTwo);
  }

  /**
   * Test utility.
   *
   */
  private void deleteDir(File file) {
    File[] contents = file.listFiles();
    if (contents != null) {
      for (File f : contents) {
        deleteDir(f);
      }
    }
    //noinspection ResultOfMethodCallIgnored
    file.delete();
  }

  private String readMessageFromLogFileOnFs() throws IOException {
    String actual;
    try (BufferedReader reader = Files.newBufferedReader(
        Paths.get(PATH + "/" + new File(PATH).list()[0]), StandardCharsets.UTF_8)) {
      actual = reader.readLine();
    }
    return actual;
  }
}
