package com.goryaninaa.winter.logger.mech;

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
public class FileSystemAccess { // NOPMD

  private final File logsDir;
  private final long bytesPerFile;
  private final int amountOfLogs;

  protected FileSystemAccess(final Properties properties) {
    this.logsDir = new File(properties.getProperty("LoggingMech.logsDirPathUrl"));
    this.bytesPerFile = Long.parseLong(properties.getProperty("LoggingMech.bytesPerFile"));
    this.amountOfLogs = Integer.parseInt(properties.getProperty("LoggingMech.amountOfLogs"));
  }

  protected void writeLog(final String logRecord) {
    prepare();
    final Path curLogPath = getCurrentLogFilePath();
    try (BufferedWriter writer = Files.newBufferedWriter(curLogPath, StandardCharsets.UTF_8,
        StandardOpenOption.APPEND)) {
      if (defineCurrentLogFileSize() != 0) {
        writer.newLine();
      }
      writer.append(logRecord);
    } catch (IOException e) {
      throw new LoggerException("Problem while working with file system", e);
    }
  }

  private void prepare() {
    prepareDir();
    prepareLogFile();
  }

  private void prepareLogFile() {
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

  private void prepareDir() {
    if (!Files.exists(logsDir.toPath()) && !logsDir.mkdirs()) {
      throw new LoggerException("Failed on directory creation");
    }
  }

  private void removeExcessFiles() {
    final String[] logFileNames = listLogDirectory();
    Arrays.sort(logFileNames);
    int fileCounter = logFileNames.length;
    int arrayCounter = 0;
    while (fileCounter > amountOfLogs) {
      fileCounter--;
      try {
        Files.delete(Paths.get(
                logsDir.getAbsolutePath() + "/" + logFileNames[arrayCounter++]));
      } catch (IOException e) {
        throw new LoggerException("File deletion failed.", e);
      }
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
    final String[] logFileNames = listLogDirectory();
    Arrays.sort(logFileNames);
    final String curLogFileName = logFileNames[logFileNames.length - 1];
    return new File(logsDir, curLogFileName);
  }

  private Path getCurrentLogFilePath() {
    return getCurrentLogFile().toPath();
  }

  private void createNewLogFile() {
    try {
      if (!new File(logsDir, generateName()).createNewFile()) {
        throw new LoggerException(
                "Failed to create a new log file, because file with such name already exists");
      }
    } catch (IOException e) {
      throw new LoggerException("Failed to create a log file", e);
    }
  }

  private String generateName() {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssn");
    final String datePartOfName = LocalDateTime.now().format(formatter);
    return "ApplicationLog" + datePartOfName + ".txt";
  }

  private String[] listLogDirectory() {
    final String[] logFileNames = logsDir.list();
    if (logFileNames != null) {
      return logFileNames;
    } else {
      throw new LoggerException("Failed to remove excess files because of list() method failed");
    }
  }

  private int countLogFiles() {
    return listLogDirectory().length;
  }
}
