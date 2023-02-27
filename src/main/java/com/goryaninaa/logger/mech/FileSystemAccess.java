package com.goryaninaa.logger.mech;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Properties;

/**
 * This class is responsible for interactions with FS, such as writing,
 * controlling amount of log files on FS and their sizes.
 *
 * @author Alex Goryanin
 */
public class FileSystemAccess {

  private final File logsDir;
  private final long bytesPerFile;
  private final int amountOfLogs;

  protected FileSystemAccess(final Properties properties) {
    this.logsDir = new File(properties.getProperty("LoggingMech.logsDirPathUrl"));
    this.bytesPerFile = Long.valueOf(properties.getProperty("LoggingMech.bytesPerFile"));
    this.amountOfLogs = Integer.valueOf(properties.getProperty("LoggingMech.amountOfLogs"));
    logsDir.mkdirs();
  }

  protected void writeLog(final String record) {
    prepare();
    final Path curLogPath = getCurrentLogFilePath();
    try (BufferedWriter writer = Files.newBufferedWriter(curLogPath, StandardCharsets.UTF_8,
        StandardOpenOption.APPEND)) {
      if (defineCurrentLogFileSize() != 0) {
        writer.newLine();
      }
      writer.append(record);
    } catch (IOException e) {
      throw new LoggerException("Problem while working with file system", e);
    }
  }

  private void prepare() {
    if (countLogFiles() == 0) {
      createNewLogFile();
    }
    if (defineCurrentLogFileSize() > bytesPerFile) {
      createNewLogFile();
    }
    if (countLogFiles() > amountOfLogs) {
      removeExcessFiles();
    }
  }

  private void removeExcessFiles() {
    final String[] logFileNames = logsDir.list();
    Arrays.sort(logFileNames);
    int fileCounter = logFileNames.length;
    int arrayCounter = 0;
    while (fileCounter > amountOfLogs) {
      fileCounter--;
      new File(logsDir.getAbsolutePath() + "/" + logFileNames[arrayCounter++]).delete(); // NOPMD
    }
  }

  private long defineCurrentLogFileSize() {
    final String curLogFilePath = getCurrentLogFile().getAbsolutePath();
    final Path path = Paths.get(curLogFilePath);
    try {
      return Files.size(path);
    } catch (IOException e) {
      throw new LoggerException("Problem while working with file system", e);
    }
  }

  private File getCurrentLogFile() {
    final String[] logFileNames = logsDir.list();
    Arrays.sort(logFileNames);
    final String curLogFileName = logFileNames[logFileNames.length - 1];
    return new File(logsDir, curLogFileName);
  }

  private Path getCurrentLogFilePath() {
    return getCurrentLogFile().toPath();
  }

  private void createNewLogFile() {
    final String logFileName = generateName();
    try {
      new File(logsDir, logFileName).createNewFile();
    } catch (IOException e) {
      throw new LoggerException("Failed to create a log file", e);
    }
  }

  private String generateName() {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssn");
    final String datePartOfName = LocalDateTime.now().format(formatter);
    return "ApplicationLog" + datePartOfName + ".txt";
  }

  private int countLogFiles() {
    return logsDir.list().length;
  }
}
