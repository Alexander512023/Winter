package com.goryaninaa.web.http.server;

import com.goryaninaa.web.http.server.entity.HttpResponse;
import java.util.concurrent.CountDownLatch;

/**
 * Stub.
 *
 * @author Alex Goryanin
 */
public class RequestHandlerStub implements RequestHandler {

  private final CountDownLatch countDownLatch;

  public RequestHandlerStub(final CountDownLatch countDownLatch) {
    this.countDownLatch = countDownLatch;
  }

  @Override
  public HttpResponse handle(final String request) {
    countDownLatch.countDown();
    try {
      countDownLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace(); // NOPMD
      Thread.currentThread().interrupt();
    }
    return new HttpResponse(HttpResponseCode.OK, request);
  }

  @Override
  public void addController(final Controller controller) {
    // TODO Auto-generated method stub
  }

}
