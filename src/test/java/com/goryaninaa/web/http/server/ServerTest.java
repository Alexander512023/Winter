package com.goryaninaa.web.http.server;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Server JUnit test class.
 *
 * @author Alex Goryanin
 */
class ServerTest {
  private RequestHandlerStub requestHandler;
  private ExecutorService executor;
  private CountDownLatch countDownLatch;
  private ClientStub client1;
  private ClientStub client2;
  private ClientStub client3;
  private ClientStub client4;
  private ClientStub client5;

  @BeforeEach
  public void init() {
    this.countDownLatch = new CountDownLatch(4);
    this.requestHandler = new RequestHandlerStub(countDownLatch);
    this.executor = Executors.newFixedThreadPool(4);
    this.client1 = new ClientStub(8000);
    this.client2 = new ClientStub(8000);
    this.client3 = new ClientStub(8000);
    this.client4 = new ClientStub(8000);
    this.client5 = new ClientStub(8001);
  }

  @Test
  void serverShouldCorrectlyHandleFourClientSimultaneously() // NOPMD
      throws InterruptedException, IOException {
    final Server server = new Server(8000, 4, requestHandler);
    new Thread(() -> {
      server.start();
    }).start();
    Thread.sleep(5);
    final Future<?> future1 = executor.submit(() -> client1.go("first request"));
    final Future<?> future2 = executor.submit(() -> client2.go("second request"));
    final Future<?> future3 = executor.submit(() -> client3.go("third request"));
    final Future<?> future4 = executor.submit(() -> client4.go("fourth request"));
    executor.shutdown();
    while (!(future1.isDone() // NOPMD
        && future2.isDone() && future3.isDone() && future4.isDone())) { // NOPMD
    }
    countDownLatch.await();
    Thread.sleep(10);
    server.shutdown();
    final Pattern pattern = Pattern.compile("\\n\\n");
    final Matcher matcher1 = pattern.matcher(client1.getResponse());
    matcher1.find();
    final Matcher matcher2 = pattern.matcher(client2.getResponse());
    matcher2.find();
    final Matcher matcher3 = pattern.matcher(client3.getResponse());
    matcher3.find();
    final Matcher matcher4 = pattern.matcher(client4.getResponse());
    matcher4.find();

    assertAll("Server working",
        () -> assertTrue(countDownLatch.getCount() == 0, "Tasks worked in series"),
        () -> assertTrue(
            client1.getResponse().substring(matcher1.end()).equals(client1.getRequest()),
            "Client #1 get wrong response"),
        () -> assertTrue(
            client2.getResponse().substring(matcher1.end()).equals(client2.getRequest()),
            "Client #2 get wrong response"),
        () -> assertTrue(
            client3.getResponse().substring(matcher1.end()).equals(client3.getRequest()),
            "Client #3 get wrong response"),
        () -> assertTrue(
            client4.getResponse().substring(matcher1.end()).equals(client4.getRequest()),
            "Client #4 get wrong response"));
  }

  @Test
  void serverShouldShutdownCorrectly() throws InterruptedException, IOException { // NOPMD
    final Server server = new Server(8001, 4, requestHandler);
    new Thread(() -> {
      server.start();
    }).start();
    Thread.sleep(5);
    final Future<?> future = executor.submit(() -> client5.go("test"));
    executor.shutdown();
    Thread.sleep(10);
    server.shutdown();
    try {
      future.get(250, TimeUnit.MILLISECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      e.printStackTrace(); // NOPMD
    }

    assertAll(() -> assertThrows(SocketException.class,
        () -> new Socket("127.0.0.1", 8000), // NOPMD
        "Server socket was not closed"),
        () -> assertFalse(client5.getRequest().equals(client1.getResponse()),
            "Server executor was not shutdown"),
        () -> assertTrue(countDownLatch.getCount() == 3, "Server was not awaiting while shutdown"));
  }

}
