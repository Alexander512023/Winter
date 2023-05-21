package com.goryaninaa.winter.web.http.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Server JUnit test class.
 *
 * @author Alex Goryanin
 */
class ServerTest {
  private ClientStub client1;
  private ClientStub client2;
  private ClientStub client3;
  private ClientStub client4;
  private Properties properties;

  @BeforeEach
  public void init() {
    this.client1 = new ClientStub(8000);
    this.client2 = new ClientStub(8000);
    this.client3 = new ClientStub(8001);
    this.client4 = new ClientStub(8002);
    properties = new Properties();
    properties.setProperty("Winter.HttpServer.ThreadsNumber", "4");
    properties.setProperty("Winter.HttpServer.Delay", "500");
  }

  @Test
  void serverShouldCorrectlyHandleTwoClientSimultaneously() // NOPMD
          throws InterruptedException, IOException {
    ExecutorService executor = Executors.newFixedThreadPool(2);
    properties.remove("Winter.HttpServer.Port");
    properties.setProperty("Winter.HttpServer.Port", "8000");
    CountDownLatch countDownLatch = new CountDownLatch(2);
    RequestHandlerStub requestHandler = new RequestHandlerStub(countDownLatch, 0);
    RequestReader requestReader = new RequestReaderStub();
    final Server server = new Server(properties, requestHandler, requestReader);
    new Thread(server::start).start();
    runTestScenario1(executor, countDownLatch);
    server.shutdown();
    assertAll("Server working",
        () -> assertEquals(0, countDownLatch.getCount(), "Tasks worked in series"),
        () -> assertEquals(client1.getRequest(), getRespSubstr(client1),
                "Client #1 get wrong response"),
        () -> assertEquals(client2.getRequest(), getRespSubstr(client2),
                "Client #2 get wrong response")
    );
  }

  @Test
  void serverShouldShutdownCorrectly() throws InterruptedException, IOException {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    properties.remove("Winter.HttpServer.Port");
    properties.setProperty("Winter.HttpServer.Port", "8001");
    CountDownLatch countDownLatch = new CountDownLatch(2);
    RequestHandlerStub requestHandler = new RequestHandlerStub(countDownLatch, 0);
    RequestReader requestReader = new RequestReaderStub();
    final Server server = new Server(properties, requestHandler, requestReader);
    new Thread(server::start).start();
    final Future<?> future = runTestScenario2(executor);
    server.shutdown();
    try {
      future.get(100, TimeUnit.MILLISECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      assertAll(
              () -> assertEquals("closed", checkSocket(), "Server socket was not " +
                      "closed"),
              () -> assertNotEquals(client3.getRequest(), client3.getResponse(), "Server executor was " +
                      "not shutdown"),
              () -> assertEquals(1, countDownLatch.getCount(), "Server was not awaiting while shutdown")
      );
    }
  }

  @Test
  void ServerShouldScheduleTimeOutCorrectly() throws IOException {
    properties.remove("Winter.HttpServer.Port");
    properties.setProperty("Winter.HttpServer.Port", "8002");
    CountDownLatch countDownLatch = new CountDownLatch(3);
    RequestHandlerStub requestHandler = new RequestHandlerStub(countDownLatch, 3000);
    RequestReader requestReader = new RequestReaderStub();
    final Server server = new Server(properties, requestHandler, requestReader);
    Executors.newSingleThreadExecutor().submit(server::start);
    final long before = System.currentTimeMillis();
    runTestScenario3();
    final long after = System.currentTimeMillis();
    server.shutdown();
    final boolean testPass = (after - before) < 750
            && getRespSubstr(client4).equals("fourth request");
    assertTrue(testPass);
  }

  private String checkSocket() throws IOException {
    Socket socket;
    try {
      socket = new Socket("127.0.0.1", 8001);
    } catch (ConnectException t) {
      return "closed";
    }
    try (socket) {
      return "not closed";
    }
  }

  private String getRespSubstr(ClientStub client) {
    final Pattern pattern = Pattern.compile("\\n\\n");
    final Matcher matcher = pattern.matcher(client.getResponse());
    if (!matcher.find()) {
      fail("Client don't get correct response form server");
    }
    return client.getResponse().substring(matcher.end());
  }

  private void runTestScenario1(ExecutorService executor, CountDownLatch countDownLatch)
          throws InterruptedException {
    executor.execute(() -> client1.go("first request"));
    executor.execute(() -> client2.go("second request"));
    executor.shutdown();
    countDownLatch.await();
  }

  private Future<?> runTestScenario2(ExecutorService executor) throws InterruptedException {
    final Future<?> future = executor.submit(() -> client3.go("test"));
    executor.shutdown();
    if (executor.awaitTermination(120, TimeUnit.MILLISECONDS)) {
      fail("Do not await long enough");
    }
    synchronized (this) {
      wait(10);
    }
    return future;
  }

  private void runTestScenario3() {
    client4.go("fourth request");
  }
}
