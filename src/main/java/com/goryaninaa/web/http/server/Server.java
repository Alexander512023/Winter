package com.goryaninaa.web.http.server;

import com.goryaninaa.logger.mech.Logger;
import com.goryaninaa.logger.mech.LoggingMech;
import com.goryaninaa.logger.mech.StackTraceString;
import com.goryaninaa.web.http.server.exception.ServerException;
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
 * Server class is responsible for accepntence of client connections and
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
      .getLogger(Server.class.getClass().getCanonicalName());
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
        executor.submit(() -> {
          run(clientSocket);
        });
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
    final StringBuffer requestString = new StringBuffer();
    int contentLength = 0;
    final Pattern patternBodyLength = Pattern.compile("Content-Length");
    final Pattern patternHeadersEnd = Pattern.compile("^$");
    while (input.ready()) {
      final String currentLine = input.readLine();
      requestString.append(currentLine).append('\n');
      final Matcher matcherBodyLength = patternBodyLength.matcher(currentLine);
      final Matcher matcherHeadersEnd = patternHeadersEnd.matcher(currentLine);
      if (matcherBodyLength.find()) {
        contentLength = Integer.valueOf(currentLine.split(":")[1].trim());
      }
      if (matcherHeadersEnd.find()) {
        appendHeaderContentToResult(input, requestString, contentLength);
      }
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug(requestString.toString());
    }
    return Optional.ofNullable(requestString.toString());
  }

  private void appendHeaderContentToResult(final BufferedReader input,
      final StringBuffer requestString, final int contentLength) throws IOException {
    final char[] charArr = new char[contentLength];
    input.read(charArr);
    for (final char symbol : charArr) {
      requestString.append(symbol);
    }
  }

  private void sendResponse(final Response response, final PrintWriter output) {
    output.println(response.getResponseString());
    output.flush();
  }
}
