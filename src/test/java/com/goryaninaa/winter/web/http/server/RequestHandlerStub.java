package com.goryaninaa.winter.web.http.server;

import com.goryaninaa.winter.Waitility;
import com.goryaninaa.winter.web.http.server.entity.HttpResponse;
import java.util.concurrent.CountDownLatch;

/**
 * Stub.
 *
 * @author Alex Goryanin
 */
public class RequestHandlerStub implements RequestHandler {

  private final CountDownLatch countDownLatch;
  private final int delay;

  public RequestHandlerStub(final CountDownLatch countDownLatch, final int delay) {
    this.countDownLatch = countDownLatch;
    this.delay = delay;
  }

  @Override
  public HttpResponse handle(final String request) {
    try {
      countDownLatch.countDown();
      Waitility.waitExecution(this, delay);
      countDownLatch.await();
    } catch (InterruptedException e) {
      return new HttpResponse(HttpResponseCode.OK, request);
    }
    return new HttpResponse(HttpResponseCode.OK, request);
  }

  @Override
  public void addController(final Controller controller) {

  }

}
