package com.goryaninaa.winter.web.http.server;

import com.goryaninaa.winter.web.http.server.entity.HttpResponse;
import com.goryaninaa.winter.web.http.server.exception.ServerException;
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
      throw new ServerException("Interrupted while waiting another threads");
    }
    return new HttpResponse(HttpResponseCode.OK, request);
  }

  @Override
  public void addController(final Controller controller) {

  }

}
