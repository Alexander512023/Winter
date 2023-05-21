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
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Server class is responsible for acceptance of client connections and
 * managing request handling through concurrent tasks.
 *
 * @author Alex Goryanin
 */
public class Server {
  private final AtomicBoolean started = new AtomicBoolean(false);
  private final ServerSocket serverSocket;
  private final ScheduledExecutorService executor;
  private final RequestHandler requestHandler;
  private final RequestReader requestReader;
  private final int delay;
  private static final Logger LOG = LoggingMech.getLogger(Server.class.getCanonicalName());

  /**
   * Constructor that receive parameters: port, number of handler threads and
   * implementation of request handler.
   *
   * @param properties     - properties for server are: "port" number of server socket, "number of
   *                       threads" that will handle client connections, "delay" - time delay before
   *                       session will be timed out
   * @param requestHandler - implementation of request handler
   * @throws IOException if creation of server socket failed
   */
  public Server(Properties properties, final RequestHandler requestHandler,
                final RequestReader requestReader)
      throws IOException {
    final int port = Integer.parseInt(properties.getProperty("Winter.HttpServer.Port"));
    final int threadsNumber = Integer.parseInt(properties.getProperty("Winter.HttpServer" +
            ".ThreadsNumber"));
    delay = Integer.parseInt(properties.getProperty("Winter.HttpServer.Delay"));
    this.executor = Executors.newScheduledThreadPool(threadsNumber);
    this.serverSocket = new ServerSocket(port);
    this.requestHandler = requestHandler;
    this.requestReader = requestReader;
  }

  /**
   * Starts an endless cycle of accepting client connections and submitting
   * requests to handler.
   */
  public void start() {
    started.set(true);
    try {
      while (started.get()) {
        runScheduledHandlerTask();
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
      closeServerSocket();
    }
    if (!executor.isShutdown()) {
      shutdownExecutor();
    }
  }

  private void runScheduledHandlerTask() throws IOException {
    final Socket clientSocket = serverSocket.accept(); // NOPMD
    Future<?> handlerTask = executor.submit(() -> run(clientSocket));
    Runnable cancelTask = () -> handlerTask.cancel(true);
    executor.schedule(cancelTask, delay, TimeUnit.MILLISECONDS);
  }

  private void run(final Socket socket) {
    if (LOG.isInfoEnabled()) {
      LOG.info("New connection accepted");
    }
    try (
        BufferedReader input = new BufferedReader(
            new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        PrintWriter output = new PrintWriter(socket.getOutputStream())) {
      handleRequest(socket, input, output);
    } catch (IOException e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("Error on handling request/n" + StackTraceString.get(e));
      }
    }
  }

  private void handleRequest(Socket socket, BufferedReader input, PrintWriter output)
          throws IOException {
    final Optional<String> request = requestReader.getRequest(input);
    if (request.isPresent()) {
      final String requestString = request.get();
      final Response response = requestHandler.handle(requestString);
      sendResponse(response, output);
      if (LOG.isInfoEnabled()) {
        LOG.info("Response with code " + response.getCode().getCode() + " was sent");
      }
    }
    socket.close();
  }

  private void sendResponse(final Response response, final PrintWriter output) {
    output.println(response.getResponseString());
    output.flush();
  }

  private void shutdownExecutor() {
    executor.shutdownNow();
    if (LOG.isInfoEnabled()) {
      LOG.info("Server threads completed correctly");
    }
  }

  private void closeServerSocket() {
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
}