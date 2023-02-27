package com.goryaninaa.logger.mech;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utility class for transferring stack trace to String.
 *
 * @author Alex Goryanin
 */
public final class StackTraceString {
  
  private StackTraceString() {
    
  }

  /**
   * Get a String interpretation of your stack trace.
   *
   * @param throwable - from which stack trace will be taken
   * @return - String representation
   */
  public static String get(final Throwable throwable) {
    final StringWriter sWriter = new StringWriter();
    final PrintWriter pWriter = new PrintWriter(sWriter);
    throwable.printStackTrace(pWriter);
    return sWriter.toString();
  }
}
