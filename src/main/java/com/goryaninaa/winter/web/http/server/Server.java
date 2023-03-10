package com.goryaninaa.winter.web.http.server;

import com.goryaninaa.winter.logger.mech.Logger;
import com.goryaninaa.winter.logger.mech.LoggingMech;
import com.goryaninaa.winter.logger.mech.StackTraceString;
import com.goryaninaa.winter.web.http.server.exception.ServerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Server class is responsible for acceptance of client connections and
 * managing request handling through concurrent tasks.
 *
 * @author Alex Goryanin
 */
public class Server {
  private final AtomicBoolean started;
  private final ServerSocket serverSocket;
  private final ExecutorService executor;
  private final RequestHandler requestHandler;
  private static final Logger LOG = LoggingMech
      .getLogger(Server.class.getCanonicalName());
  private static final int TC_TIME_OUT = 50;

  /**
   * Constructor that receive parameters: port, number of handler threads and
   * implementation of request handler.
   *
   * @param port           - port number of server socket
   * @param threadsNumber  - number of threads that will handle client connections
   * @param requestHandler - implementation of request handler
   * @throws IOException if creation of server socket failed
   */
  public Server(final int port, final int threadsNumber, final RequestHandler requestHandler)
      throws IOException {
    this.requestHandler = requestHandler;
    this.executor = Executors.newFixedThreadPool(threadsNumber);
    this.serverSocket = new ServerSocket(port);
    started = new AtomicBoolean(true);
  }

  /**
   * Starts an endless cycle of accepting client connections and submitting
   * requests to handler.
   */
  public void start() {
    try {
      while (started.get()) {
        final Socket clientSocket = serverSocket.accept(); // NOPMD
        executor.submit(() -> run(clientSocket));
      }
    } catch (IOException e) {
      if (LOG.isErrorEnabled()) {
        LOG.error(StackTraceString.get(e));
      }
      throw new ServerException("Connection failed", e);
    }
  }

  /**
   * Ends cycle of client connection acceptance, close server socket and shutdown
   * executor.
   */
  public void shutdown() {
    started.set(false); // NOPMD
    if (!serverSocket.isClosed()) {
      try {
        serverSocket.close();
        if (LOG.isInfoEnabled()) {
          LOG.info("Server shut down correctly");
        }
      } catch (IOException e) {
        if (LOG.isErrorEnabled()) {
          LOG.error(StackTraceString.get(e));
        }
      }
    }
    if (!executor.isShutdown()) {
      executor.shutdownNow();
      if (LOG.isInfoEnabled()) {
        LOG.info("Server threads completed correctly");
      }
    }
  }

  private void run(final Socket socket) {
    if (LOG.isInfoEnabled()) {
      LOG.info("New connection accepted");
    }
    try (
        BufferedReader input = new BufferedReader(
            new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        PrintWriter output = new PrintWriter(socket.getOutputStream())) {
      final Optional<String> request = getRequest(input);
      if (request.isPresent()) {
        final String requestString = request.get();
        final Response response = requestHandler.handle(requestString);
        sendResponse(response, output);
        if (LOG.isInfoEnabled()) {
          LOG.info("Response with code " + response.getCode().getCode() + " was sent");
        }
      }
      socket.close();
    } catch (IOException e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("Error on handling request/n" + StackTraceString.get(e));
      }
    }
  }

  private Optional<String> getRequest(final BufferedReader input) throws IOException {
    final long before = System.currentTimeMillis();
    boolean isTechConn = false;
    while (!input.ready()) {
      final long after = System.currentTimeMillis();
      if (after - before > TC_TIME_OUT) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Technical connection handled");
        }
        isTechConn = true;
        break;
      }
    }
    return isTechConn ? Optional.empty() : readRequest(input);
  }

  private Optional<String> readRequest(final BufferedReader input) throws IOException {
    final String requestString = getRequestString(input);
    if (LOG.isDebugEnabled()) {
      LOG.debug(requestString);
    }
    return Optional.of(requestString);
  }

  private String getRequestString(BufferedReader input) throws IOException {
    final Pattern patternBodyLength = Pattern.compile("Content-Length");
    final Pattern patternHeadersEnd = Pattern.compile("^$");
    return getRequestString(input, patternBodyLength, patternHeadersEnd);
  }

  private String getRequestString(
          BufferedReader input, Pattern patternBodyLength, Pattern patternHeadersEnd)
          throws IOException {
    final StringBuilder requestString = new StringBuilder();
    int contentLength = 0;
    while (input.ready()) {
      final String currentLine = input.readLine();
      requestString.append(currentLine).append('\n');
      final Matcher matcherBodyLength = patternBodyLength.matcher(currentLine);
      final Matcher matcherHeadersEnd = patternHeadersEnd.matcher(currentLine);
      if (matcherBodyLength.find()) {
        contentLength = Integer.parseInt(currentLine.split(":")[1].trim());
      }
      if (matcherHeadersEnd.find()) {
        requestString.append(requestBodyToString(input, contentLength));
      }
    }
    return requestString.toString();
  }

  private String requestBodyToString(final BufferedReader input, final int contentLength)
          throws IOException {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < contentLength; i++) {
      char value = (char) input.read();
      result.append(value);
    }
    return result.toString();
  }

  private void sendResponse(final Response response, final PrintWriter output) {
    output.println(response.getResponseString());
    output.flush();
  }
}