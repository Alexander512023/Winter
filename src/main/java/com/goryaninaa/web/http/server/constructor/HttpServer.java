package com.goryaninaa.web.http.server.constructor;

import com.goryaninaa.web.http.server.Controller;
import com.goryaninaa.web.http.server.RequestHandler;
import com.goryaninaa.web.http.server.Server;
import com.goryaninaa.web.http.server.entity.IncomingRequest;
import com.goryaninaa.web.http.server.entity.OutgoingResponse;
import com.goryaninaa.web.http.server.json.JsonDeserializer;
import com.goryaninaa.web.http.server.request.handler.Deserializer;
import com.goryaninaa.web.http.server.request.handler.HttpRequestHandler;
import com.goryaninaa.web.http.server.request.handler.RequestPreparator;
import com.goryaninaa.web.http.server.request.handler.ResponsePreparator;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * This class combines classes of server package in one HTTP server.
 *
 * @author Alex Goryanin
 */
public class HttpServer {
  private final Server server;

  /**
   * This is constructor that receives properties and controllers to tune
   * underlying classes.
   *
   * @param properties  - Java standard properties object passed from client code
   * @param controllers - List of controllers, implemented on the client code side
   * @throws IOException - thrown if failed to instantiate server socket
   */
  public HttpServer(final Properties properties, final List<Controller> controllers)
      throws IOException {
    final RequestPreparator inc = new IncomingRequest();
    final ResponsePreparator out = new OutgoingResponse();
    final Deserializer deserializer = new JsonDeserializer();
    final RequestHandler requestHandler = new HttpRequestHandler(inc, out, deserializer);
    for (final Controller controller : controllers) {
      requestHandler.addController(controller);
    }
    final int port = Integer.valueOf(properties.getProperty("HttpServer.Port"));
    final int threadsNumber = Integer.valueOf(properties.getProperty("HttpServer.ThreadsNumber"));
    this.server = new Server(port, threadsNumber, requestHandler);
  }

  public void start() {
    server.start();
  }
}
